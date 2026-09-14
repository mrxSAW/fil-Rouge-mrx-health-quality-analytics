import api from "../api/axiosConfig";

async function downloadReport(url, params, filename) {
  let response;

  try {
    response = await api.get(url, { params,responseType: "blob", });
  } catch (error) {
    let message = "Impossible de télécharger le rapport.";

    
    if (error.response?.data instanceof Blob) {
      try {
        const text = await error.response.data.text();
        const data = JSON.parse(text);

        message = data.message || message;
      } catch {
        message = "Impossible de télécharger le rapport.";
      }
    } else if (error.response?.data?.message) {
      message = error.response.data.message;
    } else if (!error.response) {
      message = "Impossible de contacter le serveur.";
    }

    throw new Error(message, { cause: error });
  }

  const fileUrl = window.URL.createObjectURL(response.data);
  const link = document.createElement("a");

  link.href = fileUrl;
  link.download = filename;

  document.body.appendChild(link);
  link.click();
  link.remove();

  setTimeout(() => {
    window.URL.revokeObjectURL(fileUrl);
  }, 1000);
}

export async function downloadIncidentPdf( departmentId, startDate, endDate) {
  await downloadReport(
    "/reports/incidents/pdf",
    {
      departmentId: departmentId || undefined,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
    },
    "rapport-incidents.pdf"
  );
}

export async function downloadAuditPdf( departmentId, startDate, endDate) {
  await downloadReport(  "/reports/audits/pdf",
    {
      departmentId: departmentId || undefined,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
    },
    "rapport-audits.pdf"
  );
}

export async function downloadIncidentExcel( departmentId, startDate,endDate) {
  await downloadReport(  "/reports/incidents/excel",
    {
      departmentId: departmentId || undefined, startDate: startDate || undefined,
      endDate: endDate || undefined,
    },
    "rapport-incidents.xlsx"
  );
}

export async function downloadMonthlyQualityPdf(year, month) {
  await downloadReport(
    "/reports/monthly-quality/pdf",
    {
      year,
      month,
    },
    `rapport-qualite-${year}-${month}.pdf`
  );
}

export async function downloadDepartmentQhsePdf(departmentId) {
  await downloadReport(
    `/reports/departments/${departmentId}/qhse/pdf`,
    {},
    `rapport-qhse-departement-${departmentId}.pdf`
  );
}