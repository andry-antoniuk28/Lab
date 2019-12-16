package com.robosh.entities;

import com.robosh.annotations.TestAnnotation;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Smartphone extends Phone {
    private float frequency;
    private int cores;
    private int ram;

    @Builder
    public Smartphone(String name, int weight, float diagonal, int memory, boolean camera,
                      float frequency, int cores, int ram) {
        super(name, weight, diagonal, memory, camera);
        this.frequency = frequency;
        this.cores = cores;
        this.ram = ram;
    }

    @TestAnnotation
    public void annotatedMethod() {
        memory *= 2;
    }
}
