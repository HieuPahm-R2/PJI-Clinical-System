package com.vietnam.pji.copnfig;

import java.util.ArrayList;
import java.util.List;

import com.vietnam.pji.model.auth.Permission;
import com.vietnam.pji.model.auth.Role;
import com.vietnam.pji.model.auth.User;
import com.vietnam.pji.repository.PermissionRepository;
import com.vietnam.pji.repository.RoleRepository;
import com.vietnam.pji.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>> INITIAL DATABASE BEGINS:");
        long countPermission = this.permissionRepository.count();
        long countRole = this.roleRepository.count();
        long countUser = this.userRepository.count();

        if (countPermission == 0) {
            ArrayList<Permission> arrResult = new ArrayList<>();
            arrResult.add(new Permission("Create a patient ", "/api/v1/add-patient", "POST", "PATIENTS"));
            arrResult.add(new Permission("Update a patient", "/api/v1/update-patient", "PUT", "PATIENTS"));
            arrResult.add(new Permission("Delete a patient ", "/api/v1/delete-patient/{id}", "DELETE", "PATIENTS"));
            arrResult.add(new Permission("Get patient by id", "/api/v1/patient/{id}", "GET", "PATIENTS"));
            arrResult.add(new Permission("Get patients with pagination", "/api/v1/patients", "GET", "PATIENTS"));

            arrResult.add(new Permission("Create a permission", "/api/v1/add-permission", "POST", "PERMISSIONS"));
            arrResult.add(new Permission("Update a permission", "/api/v1/update-permission", "PUT", "PERMISSIONS"));
            arrResult.add(
                    new Permission("Delete a permission", "/api/v1/delete-permission/{id}", "DELETE", "PERMISSIONS"));
            arrResult.add(new Permission("Get a permission by id", "/api/v1/permission/{id}", "GET", "PERMISSIONS"));
            arrResult
                    .add(new Permission("Get permission with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arrResult.add(new Permission("Create a role", "/api/v1/add-role", "POST", "ROLES"));
            arrResult.add(new Permission("Update a role", "/api/v1/update-role", "PUT", "ROLES"));
            arrResult.add(new Permission("Delete a role", "/api/v1/delete-role/{id}", "DELETE", "ROLES"));
            arrResult.add(new Permission("Get role by id", "/api/v1/role/{id}", "GET", "ROLES"));
            arrResult.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arrResult.add(new Permission("Create a user", "/api/v1/add-user", "POST", "USERS"));
            arrResult.add(new Permission("Update a user", "/api/v1/update-user", "PUT", "USERS"));
            arrResult.add(new Permission("Delete a user", "/api/v1/delete-user/{id}", "DELETE", "USERS"));
            arrResult.add(new Permission("Get a user by id", "/api/v1/user/{id}", "GET", "USERS"));
            arrResult.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            arrResult.add(new Permission("Upload video", "/api/v1/upload/video", "POST", "EPISODES"));
            arrResult.add(new Permission("Upload file", "/api/v1/files", "POST", "FILES"));


            this.permissionRepository.saveAll(arrResult);
        }
        if (countRole == 0) {
            List<Permission> permissions = this.permissionRepository.findAll();

            Role initRole = new Role();
            initRole.setName("ADMIN");
            initRole.setDescription("Contain full of permissions on this web service");
            initRole.setActive(true);
            initRole.setPermissions(permissions);

            this.roleRepository.save(initRole);
        }
        if (countUser == 0) {
            User initUser = new User();
            initUser.setFullName("Pham Trung Hiếu");
            initUser.setEmail("admin@gmail.com");
            initUser.setPassword(this.passwordEncoder.encode("123456"));

            Role userRole = this.roleRepository.findByName("ADMIN");
            if (userRole != null) {
                initUser.setRole(userRole);
            }
            this.userRepository.save(initUser);
        }
        if (countRole > 0 && countPermission > 0 && countUser > 0) {
            System.out.println("SKIP INITIAL DATABASE");
        } else {
            System.out.println("END TASK");
        }
    }
}
