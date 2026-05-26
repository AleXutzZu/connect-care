package me.alexutzzu.teledon.model.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public interface HighestDonationInfoDto {
    String getCharityName();
    Double getAmount();
}
