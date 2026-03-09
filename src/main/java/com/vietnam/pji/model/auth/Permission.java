package com.vietnam.pji.model.auth;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vietnam.pji.model.AbstractEntity;
import com.vietnam.pji.utils.SecurityUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "permissions", uniqueConstraints = {
        @UniqueConstraint(name = "permissions_api_path_method_key", columnNames = {"api_path", "method"})
})
@NoArgsConstructor
public class Permission extends AbstractEntity<Long> implements Serializable {

    @Column(name = "name")
    private String name;
    @Column(name = "apiPath")
    private String apiPath;
    @Column(name = "method")
    private String method;
    @Column(name = "module")
    private String module;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;

    // handle for Auto create new user with sample permission
    public Permission(String name, String apiPath, String method, String module) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;


}
