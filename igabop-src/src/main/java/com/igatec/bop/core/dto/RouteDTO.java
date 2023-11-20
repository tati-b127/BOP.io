package com.igatec.bop.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RouteDTO {
    private String route;
    private List<String> content;
}
