package ir.snapp.box.interview.task.model.mapper

import ir.snapp.box.interview.task.model.domain.AddressModel
import ir.snapp.box.interview.task.model.entity.AddressEntity
import org.mapstruct.Mapper

@Mapper
interface AddressMapper : EntityMapper<AddressEntity, AddressModel> {
}