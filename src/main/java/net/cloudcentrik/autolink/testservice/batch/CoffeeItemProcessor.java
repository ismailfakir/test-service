package net.cloudcentrik.autolink.testservice.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

@Log4j2
public class CoffeeItemProcessor implements ItemProcessor<Coffee, Coffee> {
    @Override
    public Coffee process(final Coffee coffee) throws Exception {
        String brand = coffee.getBrand().toUpperCase();
        String origin = coffee.getOrigin().toUpperCase();
        String chracteristics = coffee.getCharacteristics().toUpperCase();

        Coffee transformedCoffee = new Coffee(brand, origin, chracteristics);
        log.info("Converting ( {} ) into ( {} )", coffee, transformedCoffee);

        return transformedCoffee;
    }
}
