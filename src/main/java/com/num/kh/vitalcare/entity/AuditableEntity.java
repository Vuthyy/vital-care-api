package com.num.kh.vitalcare.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

  @CreatedBy
  @Column(name = "created_by", nullable = false, updatable = false, length = 100)
  private String createdBy;

  @CreatedDate
  @Column(name = "created_on", nullable = false, updatable = false)
  private Instant createdOn;

  @LastModifiedBy
  @Column(name = "modified_by", length = 100)
  private String modifiedBy;

  @LastModifiedDate
  @Column(name = "modified_on")
  private Instant modifiedOn;
}
