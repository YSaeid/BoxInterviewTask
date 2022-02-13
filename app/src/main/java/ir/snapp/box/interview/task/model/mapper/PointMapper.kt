package ir.snapp.box.interview.task.model.mapper

import ir.snapp.box.interview.task.model.domain.PointModel
import ir.snapp.box.interview.task.model.entity.PointEntity
import org.mapstruct.Mapper

@Mapper
interface PointMapper : EntityMapper<PointEntity, PointModel>