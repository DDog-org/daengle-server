package ddog.domain.groomer.dto;

import ddog.domain.groomer.enums.GroomingBadge;

import java.util.List;

public class SearchGroomerResultDto {

    private final Long accountId;
    private final String name;
    private final String imageUrl;
    private final List<GroomingBadge> badges;

    public SearchGroomerResultDto(Long accountId, String name, String imageUrl, List<GroomingBadge> badges) {
        this.accountId = accountId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.badges = badges;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<GroomingBadge> getBadges() {
        return badges;
    }
}
