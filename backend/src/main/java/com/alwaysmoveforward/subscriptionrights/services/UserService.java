package com.alwaysmoveforward.subscriptionrights.services;

import com.alwaysmoveforward.subscriptionrights.data.repositories.UserRepository;
import com.alwaysmoveforward.subscriptionrights.domainmodel.Auth0UserProfile;
import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import com.alwaysmoveforward.subscriptionrights.exceptions.NotFoundException;
import com.alwaysmoveforward.subscriptionrights.security.AdminEmailsProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdminEmailsProperties adminEmailsProperties;

    public UserService(UserRepository userRepository, AdminEmailsProperties adminEmailsProperties) {
        this.userRepository = userRepository;
        this.adminEmailsProperties = adminEmailsProperties;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    /**
     * Looks up a User by the Auth0 identity that just logged in -- first by subject,
     * then by email -- and provisions a new User record the first time this identity
     * is seen. Either way, if their email is in the configured admin-emails list and
     * they aren't already an admin, this login promotes them.
     */
    @Transactional
    public User findOrCreateFromAuth0Profile(Auth0UserProfile profile) {
        Optional<User> existing = userRepository.findByIdentityProviderSubject(profile.getSubject());
        if (existing.isEmpty()) {
            existing = userRepository.findByEmail(profile.getEmail());
        }

        User user;
        if (existing.isPresent()) {
            user = existing.get();
            user.refreshFromAuth0Profile(profile);
        } else {
            user = User.provision(profile);
        }

        if (!user.isAdmin() && adminEmailsProperties.isAdminEmail(user.getEmail())) {
            user.promoteToAdmin();
        }

        return userRepository.save(user);
    }

    @Transactional
    public User promoteToAdmin(Long id) {
        User user = getUser(id);
        user.promoteToAdmin();
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        getUser(id);
        userRepository.deleteById(id);
    }
}
