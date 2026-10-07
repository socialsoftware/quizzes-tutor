package pt.ulisboa.tecnico.socialsoftware.tutor.questiongeneration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * One change to the sections of a document: op is rename, merge, shift or split. Title is the new
 * name (rename) or the name of the new section (split), delta moves a section a level up (-1) or
 * down (+1), paragraph is where a split starts the new section.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AqgOutlineEditDto(
        @NotBlank String op,
        @NotBlank @Size(max = 1000) String path,
        @Size(max = 200) String title,
        Integer delta,
        Integer paragraph)
        implements Serializable {
}
