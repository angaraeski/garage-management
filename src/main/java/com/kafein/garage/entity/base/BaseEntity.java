package com.kafein.garage.entity.base;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Column(name = "CREATE_TAR")
    @CreationTimestamp
    private Date createTar;

    @Column(name = "UPDATE_TAR")
    @UpdateTimestamp
    private Date updateTar;

    @Version
    private int version;
}