package ir.snapp.box.interview.task.model.mapper

import org.mapstruct.Mapping

// base generic mapper class that map entity model
// to domain model based on mapstruct library
interface EntityMapper<Entity, DomainModel> {

    // map source value to target value
    // if you want to multi mapping use @Mapping(list(value))
    @Mapping(source = "entityId", target = "modelId")
    fun mapFromEntityToDomainModel(entity: Entity?): DomainModel?

}