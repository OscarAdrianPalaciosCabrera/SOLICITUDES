def build_message(messagedic: dict, state_str: str) -> str:
    return f"""
    Buen día.
    
    Cliente identificado con el documento No. {messagedic.get('identityDocument', 'N/A')}, CrediYa le informa que su préstamo: {messagedic.get('loanId', 'N/A')}, 
    se encuentra en estado: {state_str}.
    
    
    Este correo ha sido enviado de forma automática por la entidad y solo es informativo. Por favor no responder
    
    En caso de tener alguna inquietud por favor comuníquese al: 60100903894674, 
    o a nuestro correo corporativo: CrediYa@gmail.com
    """
