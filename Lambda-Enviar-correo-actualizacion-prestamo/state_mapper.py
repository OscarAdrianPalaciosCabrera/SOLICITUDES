def map_state(state_value: str) -> str:
    mapping = {
        "1": "APROBADO",
        "2": "RECHAZADO"
    }
    return mapping.get(str(state_value), "DESCONOCIDO")