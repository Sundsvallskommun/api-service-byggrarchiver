package se.sundsvall.casemanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"category", "name", "extension", "mimeType", "note", "file"})
@Getter
@Setter
public class Attachment {
    @Getter
    @Schema(readOnly = true)
    private String id;
    @NotNull
    private AttachmentCategory category;
    @NotBlank
    @Schema(example = "Namn på dokumentet")
    private String name;
    @Schema(example = "En anteckning.")
    private String note;
    @NotBlank
    @Pattern(regexp = "^\\.(bmp|gif|tif|tiff|jpeg|jpg|png|htm|html|pdf|rtf|docx|txt|xlsx|odt|ods)$", message = "extension must be valid. Must match regex: {regexp}")
    @Schema(example = ".pdf")
    private String extension;
    @Pattern(regexp = "^(application|image|text)/(bmp|gif|tiff|jpeg|png|html|pdf|rtf|vnd.openxmlformats-officedocument.wordprocessingml.document|plain|vnd.openxmlformats-officedocument.spreadsheetml.sheet|vnd.oasis.opendocument.text|vnd.oasis.opendocument.spreadsheet)$", message = "mimeType must be valid. Must match regex: {regexp}")
    @Schema(example = "application/pdf")
    private String mimeType;
    @NotBlank
    @Pattern(regexp = "^(?:[A-Za-z\\d+/]{4})*(?:[A-Za-z\\d+/]{3}=|[A-Za-z\\d+/]{2}==)?$", message = "file must be a valid Base64 string. Plain text - only the Base64 value.")
    @Schema(type = SchemaType.STRING, format = "byte", description = "Base64-encoded file (plain text)", example = "base64")
    private String file;
    private ArchiveMetadata archiveMetadata;
}
