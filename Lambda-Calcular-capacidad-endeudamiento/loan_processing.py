from decimal import Decimal
from calculator import calculate_monthly
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

RISK_PERCENTAGE = Decimal('0.35')




def evaluate_loan(payload):

    base_salary = Decimal(payload.get("baseSalary", 0))
    capacity_max = base_salary * RISK_PERCENTAGE

    active_loans = payload.get("activeLoans", [])
    current_debt = sum(calculate_monthly(l) for l in active_loans)

    available_capacity = capacity_max - current_debt

    new_loan = {
        "amount": Decimal(payload.get("loanAmount")),
        "interestRate": Decimal(payload.get("interestRate")) / 100,
        "timeLimit": int(payload.get("timeLimit"))
    }
    new_loan_monthly = calculate_monthly(new_loan)
    logger.info(f"new_loan_monthly: {new_loan_monthly}")

    if new_loan_monthly <= available_capacity:
        if new_loan['amount'] > 5 * base_salary:
            return 3  # REVISION MANUAL
        return 1  # APROBADO
    return 2  # RECHAZADO



