package com.alwaysmoveforward.subscriptionrights.data.repositories;

import com.alwaysmoveforward.subscriptionrights.data.Entities.UserEntity;
import com.alwaysmoveforward.subscriptionrights.data.dao.UserDAO;
import com.alwaysmoveforward.subscriptionrights.data.mapper.UserMapper;
import com.alwaysmoveforward.subscriptionrights.domainmodel.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final UserDAO userDAO;
    private final UserMapper userMapper;

    public UserRepository(UserDAO userDAO, UserMapper userMapper) {
        this.userDAO = userDAO;
        this.userMapper = userMapper;
    }

    public Optional<User> findById(Long id) {
        return userDAO.findById(id).map(userMapper::toDomainModel);
    }

    public List<User> findAll() {
        return userDAO.findAll().stream().map(userMapper::toDomainModel).toList();
    }

    public void deleteById(Long id) {
        userDAO.deleteById(id);
    }

    public Optional<User> findByIdentityProviderSubject(String identityProviderSubject) {
        return userDAO.findByIdentityProviderSubject(identityProviderSubject).map(userMapper::toDomainModel);
    }

    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email).map(userMapper::toDomainModel);
    }

    public User save(User user) {
        UserEntity saved = userDAO.save(userMapper.toEntity(user));
        return userMapper.toDomainModel(saved);
    }
}
