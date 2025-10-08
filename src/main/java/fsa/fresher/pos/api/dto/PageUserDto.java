package fsa.fresher.pos.api.dto;

import java.util.List;

public class PageUserDto {
    private List<UserDto> content;
    private PageMetadataDto metadata;

    public List<UserDto> getContent() { return content; }
    public void setContent(List<UserDto> content) { this.content = content; }
    public PageMetadataDto getMetadata() { return metadata; }
    public void setMetadata(PageMetadataDto metadata) { this.metadata = metadata; }
}
