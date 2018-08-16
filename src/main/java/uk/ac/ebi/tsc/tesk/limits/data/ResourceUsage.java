package uk.ac.ebi.tsc.tesk.limits.data;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author aniewielska
 * @since 13/08/2018
 */
@Getter
public class ResourceUsage {

    public static ResourceUsage ZERO = new ResourceUsage(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

    private BigDecimal durationHours;
    private BigDecimal cpuHours;
    private BigDecimal memoryGBHours;
    private BigDecimal storageGBHours;

    public ResourceUsage(BigDecimal durationHours, BigDecimal cpuHours, BigDecimal memoryGBHours, BigDecimal storageGBHours) {
        this.durationHours = durationHours;
        this.cpuHours = cpuHours;
        this.memoryGBHours = memoryGBHours;
        this.storageGBHours = storageGBHours;
    }

    public ResourceUsage add(ResourceUsage augend) {
        return new ResourceUsage(this.durationHours.add(augend.durationHours),
                this.cpuHours.add(augend.cpuHours),
                this.memoryGBHours.add(augend.memoryGBHours),
                this.storageGBHours.add(augend.storageGBHours));
    }
}
