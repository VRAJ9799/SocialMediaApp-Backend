package com.vraj.socialmediaapp.services;

import com.vraj.socialmediaapp.dtos.ChangePasswordDto;
import com.vraj.socialmediaapp.dtos.UserDto;
import com.vraj.socialmediaapp.exceptions.StatusException;
import com.vraj.socialmediaapp.helpers.JwtHelper;
import com.vraj.socialmediaapp.models.AppUser;
import com.vraj.socialmediaapp.models.commons.Paging;
import com.vraj.socialmediaapp.models.entities.Follower;
import com.vraj.socialmediaapp.models.entities.User;
import com.vraj.socialmediaapp.models.entities.enums.ActivityType;
import com.vraj.socialmediaapp.repositories.interfaces.FollowerRepository;
import com.vraj.socialmediaapp.repositories.interfaces.UserRepository;
import com.vraj.socialmediaapp.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository _userRepository;
    private final FollowerRepository _followerRepository;
    private final ModelMapper _modelMapper;
    private final PasswordEncoder _passwordEncoder;
    private final JwtHelper _jwtHelper;

    public UserServiceImpl(UserRepository userRepository, FollowerRepository followerRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JwtHelper jwtHelper) {
        _userRepository = userRepository;
        _followerRepository = followerRepository;
        _modelMapper = modelMapper;
        _passwordEncoder = passwordEncoder;
        _jwtHelper = jwtHelper;
    }

    @Override
    public Page<UserDto> getAllUsers(Paging<UserDto> userPaging) {
        PageRequest pageRequest = PageRequest.of(userPaging.getPageNo() - 1, userPaging.getLimit(), Sort.by(userPaging.getSortOrder(), userPaging.getSortBy()));
        Page<User> users = _userRepository.findAll(new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                for (String key : userPaging.getFilters().keySet()) {
                    Object value = userPaging.getFilters().get(key);
                    if (value instanceof Boolean b) {
                        predicates.add(
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(root.get(key), b)
                                )
                        );
                    } else if (value instanceof String || value instanceof Number) {
                        predicates.add(
                                criteriaBuilder.and(
                                        criteriaBuilder.like(root.get(key), "%" + value + "%", 'i')
                                )
                        );
                    }
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);
        Page<UserDto> userDtos = users.map(u -> _modelMapper.map(u, UserDto.class));
        return userDtos;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Getting user with id {}.", id);
        Optional<User> user = _userRepository.findById(id);
        if (user.isEmpty()) {
            throw new StatusException("Invalid user id.", HttpStatus.BAD_REQUEST);
        }
        UserDto userDto = _modelMapper.map(user.get(), UserDto.class);
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = _userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new StatusException("Invalid Email.", HttpStatus.BAD_REQUEST)
        );
        UserDto userDto = _modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public User save(User user) {
        user = _userRepository.save(user);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return _userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return _userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public void followUnFollowUser(ActivityType activityType, Long follower_id) {
        Long curr_user = 1L;
        switch (activityType) {
            case FOLLOW -> {
                Follower follower = new Follower(
                        _userRepository.getReferenceById(curr_user),
                        _userRepository.getReferenceById(follower_id)
                );
                _followerRepository.save(follower);
            }
            case UNFOLLOW -> {
                Follower follower = new Follower(
                        _userRepository.getReferenceById(curr_user),
                        _userRepository.getReferenceById(follower_id)
                );
                _followerRepository.delete(follower);
            }
        }
    }

    @Override
    public Set<UserDto> getFollowers(Long userId) {
        Set<User> followers = _followerRepository.getFollowers(userId);
        return getUserDtos(followers);
    }

    @Override
    public Set<UserDto> getFollowings(Long userId) {
        Set<User> followings = _followerRepository.getFollowings(userId);
        return getUserDtos(followings);
    }

    @Override
    public String generateAccessToken(String email) {
        UserDetails userDetails = loadUserByUsername(email);
        String access_token = _jwtHelper.doGenerateToken(userDetails);
        return access_token;
    }

    @Override
    public void emailVerification(boolean isEmailVerified, Long id) {
        Date verifiedOn = null;
        if (isEmailVerified)
            verifiedOn = new Date();
        _userRepository.updateEmailVerification(isEmailVerified, verifiedOn, id);
    }


    @Override
    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto) {
        User user = _userRepository.findById(changePasswordDto.getId())
                .orElseThrow(
                        () -> new StatusException("Invalid user id.", HttpStatus.BAD_REQUEST)
                );
        if (!user.getPassword().equals(_passwordEncoder.encode(changePasswordDto.getOldPassword()))) {
            throw new StatusException("Wrong Old Password.", HttpStatus.BAD_REQUEST);
        }
        _userRepository.updateUserPassword(user.getId(), _passwordEncoder.encode(changePasswordDto.getNewPassword()));
    }

    @Override
    @Transactional
    public void updateLockedOutAttempt(int attempt, String email, boolean isLockedOut) {
        log.info("updating locked-out attempt for user with email {}.", email);
        Date expireOn = null;
        if (isLockedOut) expireOn = Date.from(
                Instant.now().plus(10, ChronoUnit.MINUTES)
        );
        _userRepository.updateLockedOutAttempt(attempt, email, expireOn, isLockedOut);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        log.info("Deleting user with id {}.", id);
        _userRepository.deleteUserById(id);
    }

    private Set<UserDto> getUserDtos(Set<User> users) {
        Set<UserDto> userDtos = new HashSet<>();
        users.forEach(following -> {
            if (!following.isDeleted()) {
                UserDto userDto = _modelMapper.map(following, UserDto.class);
                userDtos.add(userDto);
            }
        });
        return userDtos;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = _userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new UsernameNotFoundException("Invalid email or password.")
        );
        return new AppUser(user);
    }
}
