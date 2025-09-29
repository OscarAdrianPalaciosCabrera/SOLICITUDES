package com.crediya.consumer;

import java.util.UUID;

public record UpdateStateDTO(UUID id,
                             int decision
                             ) {
}
