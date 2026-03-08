package com.vietnam.pji.controller.medical;

import com.vietnam.pji.dto.request.ImageResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.ImageResult;
import com.vietnam.pji.services.ImageResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@Validated
@Tag(name = "Image Result Controller")
@RequiredArgsConstructor
public class ImageResultController {

    private final ImageResultService imageResultService;

    @PostMapping("/image-results")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<ImageResult> createImageResult(@Valid @RequestBody ImageResultRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Image result created successfully",
                imageResultService.create(request));
    }

    @PutMapping("/image-results/{id}")
    public ResponseData<ImageResult> updateImageResult(
            @PathVariable Long id, @Valid @RequestBody ImageResultRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Image result updated successfully",
                imageResultService.update(id, request));
    }

    @GetMapping("/image-results/{id}")
    public ResponseData<ImageResult> getImageResult(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch image result successfully",
                imageResultService.getById(id));
    }

    @DeleteMapping("/image-results/{id}")
    public ResponseData<Void> deleteImageResult(@PathVariable Long id) {
        imageResultService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Image result deleted successfully");
    }

    @GetMapping("/episodes/{episodeId}/image-results")
    public ResponseData<PaginationResultDTO> getImageResultsByEpisode(
            @PathVariable Long episodeId, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch image results successfully",
                imageResultService.getByEpisode(episodeId, pageable));
    }
}
