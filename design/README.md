# Design Documents — Pet Care Reminder System

## Export Instructions

### PlantUML → PNG
```bash
java -jar plantuml.jar design/plantuml/*.puml
java -jar plantuml.jar design/c4/*.puml
```

### draw.io → PNG
1. Open `design/drawio/deployment.drawio` at [app.diagrams.net](https://app.diagrams.net)
2. File → Export As → PNG (300 DPI)
3. Save as `design/drawio/deployment.png`

### ProjectLibre → Export
1. Open `design/projectlibre/project.xml` in [ProjectLibre](https://www.projectlibre.com)

### UMPLE → Verify
1. Open `design/umple/model.ump` at [try.umple.org](https://cruise.umple.org/umpleonline/)
2. Paste contents → Generate Diagram

---

## Midterm Checklist

| # | Deliverable | Path | Format |
|---|------------|------|--------|
| 1 | Class Diagram | `plantuml/class.puml` | .puml + .png |
| 2 | Sequence Diagrams (x4) | `plantuml/seq-login.puml`, `seq-create-entry.puml`, `seq-switch-storage.puml`, `seq-search-pets.puml` | .puml + .png |
| 3 | Use-Case Diagram | `plantuml/usecase.puml` | .puml + .png |
| 4 | ER Diagram | `plantuml/er.puml` | .puml + .png |
| 5 | State Diagram | `plantuml/state-reminder.puml` | .puml + .png |
| 6 | Gantt Chart | `plantuml/gantt.puml` | .puml + .png |
| 7 | Activity Diagrams (x8) | `plantuml/activity-*.puml` | .puml + .png |
| 8 | C4 Model (x3) | `c4/c4-context.puml`, `c4-container.puml`, `c4-component.puml` | .puml + .png |
| 9 | UMPLE Domain Model | `umple/model.ump` | .ump |
| 10 | Console Wireframes | `figma/console/*.png` | PNG |
| 11 | Deployment Diagram | `drawio/deployment.drawio` | .drawio + .png |
| 12 | Project Plan | `projectlibre/project.xml` | .xml |

## File Structure
```
design/
├── README.md
├── plantuml/               ← 17 .puml + 17 .png
│   ├── class.puml
│   ├── usecase.puml
│   ├── er.puml
│   ├── state-reminder.puml
│   ├── gantt.puml
│   ├── seq-login.puml
│   ├── seq-create-entry.puml
│   ├── seq-switch-storage.puml
│   ├── seq-search-pets.puml
│   └── activity-*.puml (8)
├── c4/                     ← 3 .puml + 3 .png
├── umple/
│   └── model.ump
├── drawio/
│   └── deployment.drawio
├── projectlibre/
│   └── project.xml
└── figma/
    ├── console/            ← 6 .png
    └── gui/                ← final phase
```
