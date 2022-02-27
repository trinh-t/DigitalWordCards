package com.digitalwordcards.data.repositories;

import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssociationRepository extends JpaRepository<CardAssociation, UUID> {
    Optional<CardAssociation> findByCardAndUser(Card card, User user);

    List<CardAssociation> findCardAssociationByCard(Card card);
    List<CardAssociation> findCardAssociationByUser(User user);
}
