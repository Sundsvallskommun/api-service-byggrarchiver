package se.sundsvall.sundsvall.casemanagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class ArchiveMetadata {
    @Getter
    @Setter
    private SystemType system;
}
