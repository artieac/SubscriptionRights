package com.alwaysmoveforward.subscriptionrights.web.API;

import com.alwaysmoveforward.subscriptionrights.services.UserService;
import com.alwaysmoveforward.subscriptionrights.web.Models.UserViewModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserViewModel> list() {
        return userService.listAll().stream().map(UserViewModel::from).toList();
    }

    @PostMapping("/{id}/promote")
    public UserViewModel promote(@PathVariable Long id) {
        return UserViewModel.from(userService.promoteToAdmin(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
