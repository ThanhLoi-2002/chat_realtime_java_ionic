package com.zalo.repository;

import com.zalo.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("""
                SELECT f FROM Friendship f
                WHERE f.user1.id = :u1
                  AND f.user2.id = :u2
                  AND f.stt = 1
            """)
    Optional<Friendship> findActiveFriendship(@Param("u1") Long u1,
                                              @Param("u2") Long u2);

    @Query("""
                SELECT f
                FROM Friendship f
                WHERE f.user1.id = :u1
                  AND f.user2.id = :u2
                  AND stt = 1 AND f.status != 'REJECTED'
            """)
    Friendship getFriend(Long u1, Long u2);// check isFriend

    @Query("""
                SELECT f FROM Friendship f
                LEFT JOIN FETCH f.user1 u1
                LEFT JOIN FETCH f.user2 u2
                WHERE (f.user1.id = :userId OR f.user2.id = :userId)
                AND f.status = 'ACCEPTED' AND f.stt = 1
            """)
    List<Friendship> findFriends(Long userId);

    @Query("""
                SELECT f FROM Friendship f
                WHERE f.status = 'PENDING'
                AND (f.user1.id = :userId OR f.user2.id = :userId)
                AND f.actionUserId != :userId AND stt = 1
            """)
    List<Friendship> findReceivedRequests(Long userId);

    @Query("""
                SELECT f FROM Friendship f
                WHERE f.status = 'PENDING'
                AND f.actionUserId = :userId AND stt = 1
            """)
    List<Friendship> findSentRequests(Long userId);
}
