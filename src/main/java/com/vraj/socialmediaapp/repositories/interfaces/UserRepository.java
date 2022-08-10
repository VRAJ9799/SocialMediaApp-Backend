package com.vraj.socialmediaapp.repositories.interfaces;

import com.vraj.socialmediaapp.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findByIdIn(Collection<Long> userIds);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.isEmailVerified = :isEmailVerified, u.emailVerifiedOn=:emailVerifiedOn , u.lastModifiedOn=current_timestamp where u.id =:id")
    void updateEmailVerification(boolean isEmailVerified, Date emailVerifiedOn, Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.isDeleted = true , u.lastModifiedOn = current_timestamp where u.id = :id")
    void deleteUserById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.password =:password , u.lastModifiedOn=current_timestamp where u.id =:id")
    void updateUserPassword(Long id, String password);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.lockedOutAttempt = :attempt , u.lockedOutExpireOn = :lockedOutExpireOn, u.isLockedOut=:isLockedOut ,u.lastModifiedOn=current_timestamp  where u.email = :email")
    void updateLockedOutAttempt(int attempt, String email, Date lockedOutExpireOn, boolean isLockedOut);
}
