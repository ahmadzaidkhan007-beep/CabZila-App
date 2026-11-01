package tech.codehunt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "appstatus")
public class AppStatus {

    // Single-row table: the row always uses id = 1
    @Id
    private int id = 1;

    // Lombok generates isAvailable() / setAvailable(boolean) for this field
    @Column(name = "is_available")
    private boolean available = true;
}
