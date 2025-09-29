def build_payment_plan_message(messagedic: dict) -> str:
    base_message = f"""
    Cliente identificado con el documento No. {messagedic.get('identityDocument', 'N/A')}, 
    CrediYa le informa que su plan de préstamos para el préstamo: {messagedic.get('loanId', 'N/A')} es el siguiente:
    """

    # Incluir plan de pagos si existe
    monthly_plan = messagedic.get("monthlyPlan")
    if monthly_plan:
        plan_lines = "\n".join([
            f"Mes {p['month']}: Pago Capital = {p['principalPayment']}, "
            f"Intereses = {p['interestPayment']}, Total = {p['totalPayment']}, "
            f"Saldo restante = {p['remainingPrincipal']}"
            for p in monthly_plan
        ])
        base_message += f"\n{plan_lines}\n"

    # Mensaje de cierre
    base_message += """
    Este correo ha sido enviado de forma automática por la entidad y solo es informativo. Por favor no responder.
    
    En caso de tener alguna inquietud, por favor comuníquese al: 60100903894674, 
    o a nuestro correo corporativo: CrediYa@gmail.com
    """

    return base_message
