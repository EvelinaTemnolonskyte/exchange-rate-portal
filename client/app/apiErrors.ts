type ApiErrorResponse = {
  message?: unknown;
  details?: unknown;
};

export class ApiRequestError extends Error {}

export async function ensureResponseOk(response: Response, fallback: string): Promise<void> {
  if (response.ok) return;

  const body = await response.json().catch(() => null) as ApiErrorResponse | null;
  let message = fallback;

  if (response.status >= 500) {
    message = "The server couldn't complete your request. Please try again shortly.";
  } else if (Array.isArray(body?.details)) {
    const details = body.details.filter((detail): detail is string => typeof detail === "string");
    if (details.length > 0) {
      message = details.map(formatValidationDetail).join(" ");
    }
  } else if (typeof body?.message === "string") {
    message = getFriendlyServerMessage(body.message, response.status, fallback);
  } else if (response.status === 404) {
    message = "The requested information couldn't be found.";
  } else if (response.status === 400) {
    message = "Please check the entered information and try again.";
  }

  throw new ApiRequestError(message);
}

export function getRequestErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof ApiRequestError) return error.message;
  if (error instanceof TypeError) {
    return "Couldn't connect to the service. Check your connection and try again.";
  }
  return fallback;
}

function getFriendlyServerMessage(message: string, status: number, fallback: string): string {
  if (message.startsWith("Currency not found:")) {
    return "That currency isn't currently available. Choose another currency.";
  }
  if (message.includes("date cannot be before")) {
    return "The end date must be the same as or later than the start date.";
  }
  if (message.includes("Amount must be non-negative")) {
    return "Enter an amount that is zero or greater.";
  }
  if (message.startsWith("Missing required parameter:") || message.startsWith("Invalid value for parameter:")) {
    return "Please check the entered information and try again.";
  }
  if (status === 404) return "The requested information couldn't be found.";
  if (status === 400) return "Please check the entered information and try again.";
  return message || fallback;
}

function formatValidationDetail(detail: string): string {
  const separator = detail.indexOf(":");
  if (separator === -1) return detail;

  const field = detail.slice(0, separator);
  const reason = detail.slice(separator + 1).trim();
  const fieldNames: Record<string, string> = {
    fromCurrency: "From currency",
    toCurrency: "To currency",
    amount: "Amount",
  };

  return `${fieldNames[field] ?? field}: ${reason}.`;
}