package com.zalo.repository;

import com.zalo.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findByUser1IdAndUser2Id(Long u1, Long u2);

    @Query("""
    SELECT f
    FROM Friendship f
    WHERE f.user1.id = :u1
      AND f.user2.id = :u2
""")
    Friendship getFriend(Long u1, Long u2);

    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.user1.id = :userId OR f.user2.id = :userId)
        AND f.status = 'ACCEPTED'
    """)
    List<Friendship> findFriends(Long userId);

    @Query("""
        SELECT f FROM Friendship f
        WHERE f.status = 'PENDING'
        AND (f.user1.id = :userId OR f.user2.id = :userId)
        AND f.actionUserId != :userId
    """)
    List<Friendship> findReceivedRequests(Long userId);

    @Query("""
        SELECT f FROM Friendship f
        WHERE f.status = 'PENDING'
        AND f.actionUserId = :userId
    """)
    List<Friendship> findSentRequests(Long userId);
}
