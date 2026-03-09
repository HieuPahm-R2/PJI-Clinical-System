package com.vietnam.pji.config;

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

            // PATIENTS
            arrResult.add(new Permission("Create a patient",                     "/api/v1/patients",        "POST",   "PATIENTS"));
            arrResult.add(new Permission("Update a patient",                     "/api/v1/patients/{id}",   "PUT",    "PATIENTS"));
            arrResult.add(new Permission("Delete a patient",                     "/api/v1/patients/{id}",   "DELETE", "PATIENTS"));
            arrResult.add(new Permission("Get patient by id",                    "/api/v1/patients/{id}",   "GET",    "PATIENTS"));
            arrResult.add(new Permission("Get patients with pagination",          "/api/v1/patients",        "GET",    "PATIENTS"));

            // EPISODES
            arrResult.add(new Permission("Create an episode",                    "/api/v1/episodes",                      "POST",   "EPISODES"));
            arrResult.add(new Permission("Update an episode",                    "/api/v1/episodes/{id}",                 "PUT",    "EPISODES"));
            arrResult.add(new Permission("Delete an episode",                    "/api/v1/episodes/{id}",                 "DELETE", "EPISODES"));
            arrResult.add(new Permission("Get episode by id",                    "/api/v1/episodes/{id}",                 "GET",    "EPISODES"));
            arrResult.add(new Permission("Get episodes with pagination",          "/api/v1/episodes",                      "GET",    "EPISODES"));
            arrResult.add(new Permission("Get episodes by patient",              "/api/v1/patients/{patientId}/episodes", "GET",    "EPISODES"));

            // MEDICAL_HISTORY
            arrResult.add(new Permission("Create medical history",               "/api/v1/episodes/{episodeId}/medical-history", "POST",  "MEDICAL_HISTORY"));
            arrResult.add(new Permission("Update medical history",               "/api/v1/episodes/{episodeId}/medical-history", "PUT",   "MEDICAL_HISTORY"));
            arrResult.add(new Permission("Get medical history",                  "/api/v1/episodes/{episodeId}/medical-history", "GET",   "MEDICAL_HISTORY"));

            // CLINICAL_RECORDS
            arrResult.add(new Permission("Create clinical record",               "/api/v1/clinical-records",                              "POST",   "CLINICAL_RECORDS"));
            arrResult.add(new Permission("Update clinical record",               "/api/v1/clinical-records/{id}",                         "PUT",    "CLINICAL_RECORDS"));
            arrResult.add(new Permission("Delete clinical record",               "/api/v1/clinical-records/{id}",                         "DELETE", "CLINICAL_RECORDS"));
            arrResult.add(new Permission("Get clinical record by id",            "/api/v1/clinical-records/{id}",                         "GET",    "CLINICAL_RECORDS"));
            arrResult.add(new Permission("Get clinical records by episode",      "/api/v1/episodes/{episodeId}/clinical-records",          "GET",    "CLINICAL_RECORDS"));

            // SURGERIES
            arrResult.add(new Permission("Create surgery",                       "/api/v1/surgeries",                       "POST",   "SURGERIES"));
            arrResult.add(new Permission("Update surgery",                       "/api/v1/surgeries/{id}",                  "PUT",    "SURGERIES"));
            arrResult.add(new Permission("Delete surgery",                       "/api/v1/surgeries/{id}",                  "DELETE", "SURGERIES"));
            arrResult.add(new Permission("Get surgery by id",                    "/api/v1/surgeries/{id}",                  "GET",    "SURGERIES"));
            arrResult.add(new Permission("Get surgeries by episode",             "/api/v1/episodes/{episodeId}/surgeries",  "GET",    "SURGERIES"));

            // LAB_RESULTS
            arrResult.add(new Permission("Create lab result",                    "/api/v1/lab-results",                         "POST",   "LAB_RESULTS"));
            arrResult.add(new Permission("Update lab result",                    "/api/v1/lab-results/{id}",                    "PUT",    "LAB_RESULTS"));
            arrResult.add(new Permission("Delete lab result",                    "/api/v1/lab-results/{id}",                    "DELETE", "LAB_RESULTS"));
            arrResult.add(new Permission("Get lab result by id",                 "/api/v1/lab-results/{id}",                    "GET",    "LAB_RESULTS"));
            arrResult.add(new Permission("Get lab results by episode",           "/api/v1/episodes/{episodeId}/lab-results",    "GET",    "LAB_RESULTS"));

            // IMAGE_RESULTS
            arrResult.add(new Permission("Create image result",                  "/api/v1/image-results",                       "POST",   "IMAGE_RESULTS"));
            arrResult.add(new Permission("Update image result",                  "/api/v1/image-results/{id}",                  "PUT",    "IMAGE_RESULTS"));
            arrResult.add(new Permission("Delete image result",                  "/api/v1/image-results/{id}",                  "DELETE", "IMAGE_RESULTS"));
            arrResult.add(new Permission("Get image result by id",               "/api/v1/image-results/{id}",                  "GET",    "IMAGE_RESULTS"));
            arrResult.add(new Permission("Get image results by episode",         "/api/v1/episodes/{episodeId}/image-results",  "GET",    "IMAGE_RESULTS"));

            // CULTURE_RESULTS
            arrResult.add(new Permission("Create culture result",                "/api/v1/culture-results",                       "POST",   "CULTURE_RESULTS"));
            arrResult.add(new Permission("Update culture result",                "/api/v1/culture-results/{id}",                  "PUT",    "CULTURE_RESULTS"));
            arrResult.add(new Permission("Delete culture result",                "/api/v1/culture-results/{id}",                  "DELETE", "CULTURE_RESULTS"));
            arrResult.add(new Permission("Get culture result by id",             "/api/v1/culture-results/{id}",                  "GET",    "CULTURE_RESULTS"));
            arrResult.add(new Permission("Get culture results by episode",       "/api/v1/episodes/{episodeId}/culture-results",  "GET",    "CULTURE_RESULTS"));

            // SENSITIVITY_RESULTS
            arrResult.add(new Permission("Create sensitivity result",            "/api/v1/sensitivity-results",                                  "POST",   "SENSITIVITY_RESULTS"));
            arrResult.add(new Permission("Update sensitivity result",            "/api/v1/sensitivity-results/{id}",                             "PUT",    "SENSITIVITY_RESULTS"));
            arrResult.add(new Permission("Delete sensitivity result",            "/api/v1/sensitivity-results/{id}",                             "DELETE", "SENSITIVITY_RESULTS"));
            arrResult.add(new Permission("Get sensitivity result by id",         "/api/v1/sensitivity-results/{id}",                             "GET",    "SENSITIVITY_RESULTS"));
            arrResult.add(new Permission("Get sensitivity results by culture",   "/api/v1/culture-results/{cultureId}/sensitivity-results",      "GET",    "SENSITIVITY_RESULTS"));

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
