package org.spring.projectjs.JPA.App.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(
        name = "app_calendar_photo",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_photo_user_day", columnNames = {"user_idx", "day_key"})
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppCalendarPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @Column(name = "user_idx", nullable = false)
    private Long userIdx;

    @Temporal(TemporalType.DATE)
    @Column(name = "day_key", nullable = false)
    private Date dayKey;                       // taken_at의 날짜 부분

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "taken_at", nullable = false)
    private Date takenAt;

    @Column(name = "file_path", length = 1000, nullable = false)
    private String filePath;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Lob
    @Column(name = "tags_json")
    private String tagsJson;

    @Lob
    @Column(name = "exif_json")
    private String exifJson;

    @Column(name = "deleted", nullable = false)
    private Integer deleted = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
        if (this.deleted == null) this.deleted = 0;
        if (this.takenAt == null) this.takenAt = now;
        if (this.dayKey == null)  this.dayKey  = new Date(now.getYear(), now.getMonth(), now.getDate());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}
