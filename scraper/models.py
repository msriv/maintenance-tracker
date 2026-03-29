from datetime import datetime
from typing import Optional
from pydantic import BaseModel, Field


class RegistryMake(BaseModel):
    id: str  # slugified name e.g. "honda"
    name: str
    country: Optional[str] = None
    logo_url: Optional[str] = None
    updated_at: datetime = Field(default_factory=datetime.utcnow)


class RegistryModel(BaseModel):
    id: str  # "{make}_{model}_{year_from}" e.g. "honda_cbr600rr_2003"
    make: str
    model: str
    year_from: int
    year_to: Optional[int] = None  # None = still in production
    variants: list[str] = Field(default_factory=list)  # e.g. ["Standard", "ABS", "SP"]
    vehicle_type: str  # "BIKE", "SCOOTER", "CAR", "TRUCK", "OTHER"
    displacement_cc: Optional[int] = None
    updated_at: datetime = Field(default_factory=datetime.utcnow)


class RegistrySchedule(BaseModel):
    id: str  # "{make}_{model}_{year_from}_{task_type}"
    make: str
    model: str
    year_from: int
    year_to: Optional[int] = None
    task_type: str  # matches Android MaintenanceTaskType enum
    task_label: str
    interval_days: Optional[int] = None
    interval_km: Optional[int] = None
    notes: Optional[str] = None
    source: str  # URL or "curated"
    updated_at: datetime = Field(default_factory=datetime.utcnow)


class BestPractice(BaseModel):
    id: str
    category: str  # "MANUFACTURER", "POST_SERVICE", "ROUTINE"
    task_type: str  # matches Android MaintenanceTaskType: ENGINE_OIL, CHAIN_LUBE, etc.
    title: str
    description: str
    applicable_to: list[str] = Field(default_factory=list)  # vehicle types: ["BIKE", "SCOOTER"] or ["ALL"]
    is_template: bool  # True = can be applied as a task template
    template_interval_days: Optional[int] = None
    template_interval_km: Optional[int] = None
    updated_at: datetime = Field(default_factory=datetime.utcnow)


class RecallAlert(BaseModel):
    id: str  # NHTSA campaign number
    make: str
    model: str
    year_from: int
    year_to: int
    component: str
    consequence: str
    remedy: str
    description: str
    severity: str  # "MINOR", "MODERATE", "CRITICAL"
    issued_date: str  # ISO date string
    nhtsa_url: str
    updated_at: datetime = Field(default_factory=datetime.utcnow)
