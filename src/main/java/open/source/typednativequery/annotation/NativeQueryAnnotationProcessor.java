package open.source.typednativequery.annotation;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/** The type Meta entity builder processor. */
@Slf4j
@SupportedAnnotationTypes({
  "javax.persistence.Entity",
  "open.source.typednativequery.annotation.TableBuilder"
})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class NativeQueryAnnotationProcessor extends AbstractProcessor {

  /** The Map of annotated elements. */
  Map<String, Element> mapOfAnnotatedElements = new HashMap<>();
  /** The Map of composite element. */
  Map<String, Element> mapOfCompositeElement = new HashMap<>();
  /** The Map of entity table. */
  Map<String, EntityTable> mapOfEntityTable = new HashMap<>();

  private void addElement(Map<String, Element> map, Set<? extends Element> set) {
    set.forEach(element -> map.putIfAbsent(element.asType().toString(), element));
  }

  /**
   * {@inheritDoc}
   *
   * @param annotations
   * @param roundEnv
   */
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    try {
      addElement(mapOfAnnotatedElements, roundEnv.getElementsAnnotatedWith(TableBuilder.class));
      addElement(mapOfCompositeElement, roundEnv.getElementsAnnotatedWith(TableCompositeId.class));
      addElement(mapOfAnnotatedElements, roundEnv.getElementsAnnotatedWith(Entity.class));
      addElement(mapOfCompositeElement, roundEnv.getElementsAnnotatedWith(Embeddable.class));
      log.debug("Elements Found : {}", mapOfAnnotatedElements.values());

      for (Element element : mapOfAnnotatedElements.values()) {
        if (!mapOfEntityTable.containsKey(element.toString())) processReferencedElement(element);
      }
    } catch (IOException | ClassNotFoundException ioException) {
      log.error("Exception while processing {0}", ioException);
      return false;
    }
    return true;
  }

  private void processReferencedElement(Element element)
      throws ClassNotFoundException, IOException {
    if (element.getKind().isClass()) {
      EntityTable table = AnnotationProcessorUtil.getEntityTable(element);
      List<? extends Element> enclosedElements = element.getEnclosedElements();
      process(enclosedElements, element, table, false);
      AnnotationProcessorUtil.buildFile(table, log, processingEnv);
      mapOfEntityTable.put(element.toString(), table);
    }
  }

  private void process(
      List<? extends Element> elementList,
      Element topElement,
      EntityTable entityTable,
      boolean isEmbedded)
      throws ClassNotFoundException, IOException {
    log.debug("Processing Top Element : {}", topElement);
    for (Element enclosedElement : elementList) {
      if (enclosedElement.getKind().isField()) {
        if (processCompositeColumn(enclosedElement, entityTable, topElement)) continue;
        if (processJoinColumns(enclosedElement, entityTable, topElement, isEmbedded)) continue;
        if (processBasicColumn(enclosedElement, entityTable, topElement, isEmbedded)) continue;
      }
    }
  }

  private boolean processCompositeColumn(
      Element element, EntityTable entityTable, Element topElement)
      throws IOException, ClassNotFoundException {
    EmbeddedId embeddedId = element.getAnnotation(EmbeddedId.class);
    TableColumn tableColumn = element.getAnnotation(TableColumn.class);
    if (Objects.isNull(embeddedId) && (Objects.isNull(tableColumn) || !tableColumn.isCompositeId()))
      return false;
    log.debug("Processing Composite Id column {} of {}", element, topElement);
    DeclaredType declaredType = (DeclaredType) element.asType();
    Element declaredTypeElement = declaredType.asElement();
    Element embeddedElement = mapOfCompositeElement.get(declaredTypeElement.asType().toString());
    process(embeddedElement.getEnclosedElements(), embeddedElement, entityTable, true);
    entityTable.containsEmbeddedId = true;
    return true;
  }

  private boolean processJoinColumns(
      Element element, EntityTable entityTable, Element topElement, boolean isEmbeddedId)
      throws IOException, ClassNotFoundException {
    JoinColumn joinColumn = element.getAnnotation(JoinColumn.class);
    JoinColumns joinColumns = element.getAnnotation(JoinColumns.class);
    TableJoinColumn tableJoinColumn = element.getAnnotation(TableJoinColumn.class);
    if (Objects.isNull(joinColumn)
        && Objects.isNull(joinColumns)
        && Objects.isNull(tableJoinColumn)) return false;

    Map<String, ColumnInfo> columnInfos = getReferencedEntityTable(element, topElement);
    if (CollectionUtils.isEmpty(columnInfos)) return false;

    String[] columnNames;
    String[] references;
    if (!Objects.isNull(joinColumns)) {
      columnNames = new String[joinColumns.value().length];
      references = new String[joinColumns.value().length];
      for (int i = 0; i < joinColumns.value().length; i++) {
        columnNames[i] = joinColumns.value()[i].name();
        references[i] = joinColumns.value()[i].referencedColumnName();
      }
    } else if (!Objects.isNull(tableJoinColumn)) {
      columnNames = tableJoinColumn.columnName();
      references = tableJoinColumn.reference();
    } else {
      String idColumn = columnInfos.keySet().stream().findFirst().get();
      columnNames = new String[] {joinColumn.name()};
      references = new String[] {joinColumn.referencedColumnName()};
      if (references[0].isEmpty()) references[0] = idColumn;
    }

    for (int i = 0; i < columnNames.length; i++) {
      String columnName =
          AnnotationProcessorUtil.buildColumnName(columnNames[i], element.toString());
      String referencedColumnName = references[i];
      log.debug("Processing Reference column {} of {}", element, topElement);
      ColumnInfo columnInfo = columnInfos.get(referencedColumnName);
      ColumnInfo newColumnInfo = AnnotationProcessorUtil.cloneColumnInfo(columnName, columnInfo);
      if (entityTable.columnInfoMap.containsKey(newColumnInfo.columnName)) continue;
      entityTable.columnInfoMap.put(newColumnInfo.columnName, newColumnInfo);
      if (isEmbeddedId) entityTable.idColumns.add(newColumnInfo.columnName);
      log.debug(
          "Completed Reference column {} of {} with {}",
          element,
          topElement,
          newColumnInfo.columnName);
    }
    return true;
  }

  private Map<String, ColumnInfo> getReferencedEntityTable(Element element, Element topElement)
      throws ClassNotFoundException, IOException {
    DeclaredType declaredType = (DeclaredType) element.asType();
    Element declaredTypeElement = declaredType.asElement();
    if (declaredTypeElement.asType().toString().equals(topElement.toString())) return null;
    Element declaredElement = mapOfAnnotatedElements.get(declaredTypeElement.asType().toString());

    if (!mapOfEntityTable.containsKey(declaredElement.toString()))
      processReferencedElement(declaredElement);
    EntityTable referencedEntityTable = mapOfEntityTable.get(declaredElement.toString());
    return referencedEntityTable.idColumns.stream()
        .map(s -> referencedEntityTable.columnInfoMap.get(s))
        .filter(columnInfo -> !Objects.isNull(columnInfo))
        .collect(Collectors.toMap(columnInfo -> columnInfo.columnName, columnInfo -> columnInfo));
  }

  private boolean processBasicColumn(
      Element element, EntityTable entityTable, Element topElement, boolean isEmbeddedId) {
    Column column = element.getAnnotation(Column.class);
    Id id = element.getAnnotation(Id.class);
    TableColumn tableColumn = element.getAnnotation(TableColumn.class);
    if (Objects.isNull(id)
        && Objects.isNull(column)
        && (Objects.isNull(tableColumn) || tableColumn.isCompositeId())) return false;
    log.debug("Processing Normal column {} of {}", element, topElement);
    ColumnInfo columnInfo = AnnotationProcessorUtil.createColumn(element);
    String columnName = Objects.isNull(column) ? null : column.name();
    columnName = Objects.isNull(tableColumn) ? columnName : tableColumn.columnName();
    columnInfo.columnName = AnnotationProcessorUtil.buildColumnName(columnName, element.toString());
    columnInfo.propertyName = element.getSimpleName().toString();
    if (entityTable.columnInfoMap.containsKey(columnInfo.columnName)) return true;
    entityTable.columnInfoMap.put(columnInfo.columnName, columnInfo);
    if (isEmbeddedId || !Objects.isNull(id)) entityTable.idColumns.add(columnInfo.columnName);
    log.debug(
        "Completed Normal column {} of {} with {}", element, topElement, columnInfo.columnName);
    return true;
  }
}
