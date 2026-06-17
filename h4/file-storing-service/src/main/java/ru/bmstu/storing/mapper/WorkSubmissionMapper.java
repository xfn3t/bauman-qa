package ru.bmstu.storing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bmstu.storing.entity.WorkSubmission;
import ru.bmstu.storing.service.dto.WorkDto;

@Mapper(componentModel = "spring")
public interface WorkSubmissionMapper {

    @Mapping(source = "id", target = "workId")
    @Mapping(source = "studentName", target = "studentName")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "fileSize", target = "fileSize")
    @Mapping(source = "contentType", target = "contentType")
    @Mapping(source = "s3Key", target = "s3Key")
    WorkDto toDto(WorkSubmission entity);
}
