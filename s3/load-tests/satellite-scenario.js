import http from "k6/http";
import { check, sleep, group } from "k6";
import { Trend, Rate, Counter } from "k6/metrics";
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";

const BASE_URL = "http://localhost:8080";

const getConstellationsDuration = new Trend(
  "get_constellations_duration",
  true,
);
const getConstellationByIdDuration = new Trend(
  "get_constellation_by_id_duration",
  true,
);
const getConstellationStatusDuration = new Trend(
  "get_constellation_status_duration",
  true,
);
const getSatellitesDuration = new Trend("get_satellites_duration", true);
const getSatelliteByIdDuration = new Trend(
  "get_satellite_by_id_duration",
  true,
);
const createConstellationDuration = new Trend(
  "create_constellation_duration",
  true,
);
const addSatelliteDuration = new Trend("add_satellite_duration", true);
const activateSatelliteDuration = new Trend(
  "activate_satellite_duration",
  true,
);
const executeMissionDuration = new Trend("execute_mission_duration", true);
const deleteConstellationDuration = new Trend(
  "delete_constellation_duration",
  true,
);

const successRate = new Rate("success_rate");
const failures = new Counter("failures");

function safeJsonParse(str) {
  try {
    return JSON.parse(str);
  } catch (_) {
    return null;
  }
}

export const options = {
  stages: [
    { duration: "15s", target: 5 },
    { duration: "15s", target: 15 },
    { duration: "30s", target: 15 },
    { duration: "15s", target: 5 },
    { duration: "10s", target: 0 },
  ],
  thresholds: {
    http_req_duration: ["p(95)<5000"],
    http_req_failed: ["rate<0.25"],
    success_rate: ["rate>0.75"],
  },
};

function getFirstConstellationId() {
  const res = http.get(`${BASE_URL}/api/constellations`);
  const body = safeJsonParse(res.body);
  if (body && body.length > 0) return body[0].id;
  return 1;
}

export default function () {
  const existingId = getFirstConstellationId();
  const constellationName = `load-test-${__VU}-${__ITER}-${Date.now()}`;
  let constellationId = null;
  let satelliteId = null;

  group("01 - GET /api/constellations", () => {
    const res = http.get(`${BASE_URL}/api/constellations`, {
      tags: { name: "getAllConstellations" },
    });
    getConstellationsDuration.add(res.timings.duration);
    const body = safeJsonParse(res.body);
    const ok = check(res, {
      "status is 200": (r) => r.status === 200,
      "response is array": () => body !== null && Array.isArray(body),
    });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("02 - GET /api/constellations/{id}", () => {
    const res = http.get(`${BASE_URL}/api/constellations/${existingId}`, {
      tags: { name: "getConstellationById" },
    });
    getConstellationByIdDuration.add(res.timings.duration);
    const body = safeJsonParse(res.body);
    const ok = check(res, {
      "status is 200": (r) => r.status === 200,
      "has id field": () => body !== null && body.id !== undefined,
    });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("03 - GET /api/constellations/{id}/status", () => {
    const res = http.get(
      `${BASE_URL}/api/constellations/${existingId}/status`,
      {
        tags: { name: "getConstellationStatus" },
      },
    );
    getConstellationStatusDuration.add(res.timings.duration);
    const ok = check(res, { "status is 200": (r) => r.status === 200 });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("04 - GET /api/constellations/{id}/satellites", () => {
    const res = http.get(
      `${BASE_URL}/api/constellations/${existingId}/satellites`,
      {
        tags: { name: "getSatellitesByConstellation" },
      },
    );
    getSatellitesDuration.add(res.timings.duration);
    const ok = check(res, { "status is 200": (r) => r.status === 200 });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("05 - POST /api/constellations", () => {
    const payload = JSON.stringify({
      name: constellationName,
      description: `Load test constellation from VU ${__VU}`,
    });
    const res = http.post(`${BASE_URL}/api/constellations`, payload, {
      headers: { "Content-Type": "application/json" },
      tags: { name: "createConstellation" },
    });
    createConstellationDuration.add(res.timings.duration);
    const ok = check(res, { "status is 201": (r) => r.status === 201 });
    successRate.add(ok);
    if (!ok) {
      failures.add(1);
      return;
    }
    const body = safeJsonParse(res.body);
    if (body) constellationId = body.id;
  });

  sleep(1);

  group("06 - POST /api/constellations/{id}/satellites", () => {
    if (!constellationId) return;
    const payload = JSON.stringify({
      name: `load-sat-${__VU}-${__ITER}`,
      batteryLevel: 0.9,
      type: "COMMUNICATION",
      bandwidth: 100.0,
      resolution: null,
    });
    const res = http.post(
      `${BASE_URL}/api/constellations/${constellationId}/satellites`,
      payload,
      {
        headers: { "Content-Type": "application/json" },
        tags: { name: "addSatellite" },
      },
    );
    addSatelliteDuration.add(res.timings.duration);
    const ok = check(res, { "status is 200": (r) => r.status === 200 });
    successRate.add(ok);
    if (!ok) {
      failures.add(1);
      return;
    }
    const body = safeJsonParse(res.body);
    if (body && body.satellites && body.satellites.length > 0) {
      satelliteId = body.satellites[body.satellites.length - 1].id;
    }
  });

  sleep(1);

  group("07 - GET /api/satellites/{id}", () => {
    const targetId = satelliteId || existingId;
    const res = http.get(`${BASE_URL}/api/satellites/${targetId}`, {
      tags: { name: "getSatelliteById" },
    });
    getSatelliteByIdDuration.add(res.timings.duration);
    const ok = check(res, {
      "status is 200 or 404": (r) => r.status === 200 || r.status === 404,
    });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("08 - POST /api/constellations/{id}/activate", () => {
    const targetId = constellationId || existingId;
    const res = http.post(
      `${BASE_URL}/api/constellations/${targetId}/activate`,
      null,
      {
        tags: { name: "activateConstellation" },
      },
    );
    activateSatelliteDuration.add(res.timings.duration);
    const ok = check(res, { "status is 200": (r) => r.status === 200 });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("09 - POST /api/constellations/{id}/execute", () => {
    const targetId = constellationId || existingId;
    const res = http.post(
      `${BASE_URL}/api/constellations/${targetId}/execute`,
      null,
      {
        tags: { name: "executeMissions" },
      },
    );
    executeMissionDuration.add(res.timings.duration);
    const ok = check(res, { "status is 200": (r) => r.status === 200 });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);

  group("10 - DELETE /api/constellations/{id}", () => {
    if (!constellationId) return;
    const res = http.del(
      `${BASE_URL}/api/constellations/${constellationId}`,
      null,
      {
        tags: { name: "deleteConstellation" },
      },
    );
    deleteConstellationDuration.add(res.timings.duration);
    const ok = check(res, { "status is 204": (r) => r.status === 204 });
    successRate.add(ok);
    if (!ok) failures.add(1);
  });

  sleep(1);
}

export function handleSummary(data) {
  const reportPath = "load-tests/report.html";
  return {
    stdout: textSummary(data, { indent: "  ", enableColors: true }),
    [reportPath]: htmlReport(data),
  };
}
