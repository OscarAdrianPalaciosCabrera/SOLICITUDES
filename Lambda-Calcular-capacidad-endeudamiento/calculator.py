def calculate_monthly(loan):
    P = loan['amount']
    i = loan['interestRate']
    n = loan['timeLimit']

    if i == 0:
        return P / n

    numerator = i * (1 + i) ** n
    denominator = ((1 + i) ** n) - 1
    return P * numerator / denominator


def generate_monthly_plan(loan):
    P = loan['amount']
    i = loan['interestRate']
    n = loan['timeLimit']
    monthly_payment = calculate_monthly(loan)

    remaining_principal = P
    plan = []

    for month in range(1, n + 1):
        interest_payment = remaining_principal * i
        principal_payment = monthly_payment - interest_payment
        remaining_principal -= principal_payment
        plan.append({
            "month": month,
            "principalPayment": round(principal_payment, 2),
            "interestPayment": round(interest_payment, 2),
            "totalPayment": round(monthly_payment, 2),
            "remainingPrincipal": round(max(remaining_principal, 0), 2)
        })
    return plan