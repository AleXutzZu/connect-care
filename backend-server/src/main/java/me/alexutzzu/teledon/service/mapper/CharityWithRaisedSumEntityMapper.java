package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import org.springframework.stereotype.Component;

@Component
public class CharityWithRaisedSumEntityMapper implements EntityMapper<Charity, CharityWithRaisedSum> {
    @Override
    public CharityWithRaisedSum toDomain(Charity entity) {
        double raisedSum = entity.getDonations().stream().mapToDouble(Donation::getAmount).sum();
        return new CharityWithRaisedSum(entity.getId(), entity.getName(), raisedSum);
    }
}
