package net.cloudcentrik.autolink.testservice.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Coffee {
    private String brand;
    private String origin;
    private String characteristics;
}
