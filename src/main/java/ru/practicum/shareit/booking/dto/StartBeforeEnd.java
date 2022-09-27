package ru.practicum.shareit.booking.dto;


import java.lang.annotation.*;

@Documented
//@Constraint(validatedBy = StartBeforeEndValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StartBeforeEnd {
}
