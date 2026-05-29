package ru.bmstu.storing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bmstu.storing.controller.request.SubmitWorkRequest;
import ru.bmstu.storing.controller.response.SubmitWorkResponse;
import ru.bmstu.storing.service.WorkSubmissionService;
import ru.bmstu.storing.service.dto.WorkDto;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
public class FileStoringController {

    private final WorkSubmissionService workSubmissionService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<SubmitWorkResponse> submitWorkMultipart(
            @RequestParam("studentName") String studentName,
            @RequestParam("file") MultipartFile file) throws Exception {

        SubmitWorkRequest request = new SubmitWorkRequest();
        request.setStudentName(studentName);
        request.setFileName(file.getOriginalFilename());
        request.setFileSize(file.getSize());
        request.setContentType(file.getContentType());
        request.setInputStream(file.getInputStream());
        request.setFileContent(null);

        SubmitWorkResponse response = workSubmissionService.submitWork(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<SubmitWorkResponse> submitWorkJson(
            @RequestBody SubmitWorkRequest request) {
        SubmitWorkResponse response = workSubmissionService.submitWork(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workId}/status")
    public ResponseEntity<Map<String, Object>> getWorkStatus(@PathVariable String workId) {
        var workOpt = workSubmissionService.getWorkById(java.util.UUID.fromString(workId));
        if (workOpt.isPresent()) {
            WorkDto w = workOpt.get();
            return ResponseEntity.ok(Map.of(
                    "workId", w.workId(),
                    "fileName", w.fileName(),
                    "fileSize", w.fileSize(),
                    "contentType", w.contentType()
            ));
        }
        return ResponseEntity.notFound().build();
    }
}
