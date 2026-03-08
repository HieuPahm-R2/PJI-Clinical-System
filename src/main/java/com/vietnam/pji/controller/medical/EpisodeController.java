package com.vietnam.pji.controller.medical;

import com.turkraft.springfilter.boot.Filter;
import com.vietnam.pji.dto.request.EpisodeRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.services.EpisodeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@Validated
@Tag(name = "Episode Controller")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @PostMapping("/episodes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<PjiEpisode> createEpisode(@Valid @RequestBody EpisodeRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Episode created successfully", episodeService.create(request));
    }

    @PutMapping("/episodes/{id}")
    public ResponseData<PjiEpisode> updateEpisode(@PathVariable Long id, @Valid @RequestBody EpisodeRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Episode updated successfully", episodeService.update(id, request));
    }

    @GetMapping("/episodes/{id}")
    public ResponseData<PjiEpisode> getEpisode(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch episode successfully", episodeService.getById(id));
    }

    @DeleteMapping("/episodes/{id}")
    public ResponseData<Void> deleteEpisode(@PathVariable Long id) {
        episodeService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Episode deleted successfully");
    }

    @GetMapping("/episodes")
    public ResponseData<PaginationResultDTO> getAllEpisodes(
            @Filter Specification<PjiEpisode> spec, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch episodes successfully", episodeService.getAll(spec, pageable));
    }

    @GetMapping("/patients/{patientId}/episodes")
    public ResponseData<PaginationResultDTO> getEpisodesByPatient(
            @PathVariable Long patientId, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch patient episodes successfully",
                episodeService.getByPatient(patientId, pageable));
    }
}
