"""
Curated OEM maintenance schedules for popular motorcycle brands.
All data is hardcoded based on publicly documented service intervals.
No HTTP requests are made by this module.
"""
from datetime import datetime
from typing import Optional

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from models import RegistrySchedule
from scrapers.base_scraper import BaseScraper


def _make_schedule(
    make: str,
    model: str,
    year_from: int,
    year_to: Optional[int],
    task_type: str,
    task_label: str,
    interval_days: Optional[int],
    interval_km: Optional[int],
    notes: Optional[str] = None,
) -> RegistrySchedule:
    schedule_id = f"{make}_{model}_{year_from}_{task_type}".lower().replace(" ", "_")
    return RegistrySchedule(
        id=schedule_id,
        make=make,
        model=model,
        year_from=year_from,
        year_to=year_to,
        task_type=task_type,
        task_label=task_label,
        interval_days=interval_days,
        interval_km=interval_km,
        notes=notes,
        source="curated",
        updated_at=datetime.utcnow(),
    )


# ---------------------------------------------------------------------------
# Honda
# ---------------------------------------------------------------------------

_HONDA_CB_SHINE_SCHEDULES = [
    _make_schedule("honda", "CB Shine", 2006, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "Use Honda recommended 10W-30 MA2 grade oil"),
    _make_schedule("honda", "CB Shine", 2006, None, "AIR_FILTER", "Air Filter Clean", 180, 6000,
                   "Clean with compressed air; replace at 12000 km"),
    _make_schedule("honda", "CB Shine", 2006, None, "SPARK_PLUG", "Spark Plug Check/Replace", None, 12000,
                   "Gap: 0.6–0.7 mm; replace if worn"),
    _make_schedule("honda", "CB Shine", 2006, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "Use O-ring safe chain lubricant"),
    _make_schedule("honda", "CB Shine", 2006, None, "CHAIN_ADJUSTMENT", "Chain Slack Adjustment", None, 1000,
                   "Free play: 20–30 mm"),
    _make_schedule("honda", "CB Shine", 2006, None, "BRAKE_OIL", "Brake Fluid Check/Replace", 365, None,
                   "Use DOT 3 brake fluid; inspect level every month"),
    _make_schedule("honda", "CB Shine", 2006, None, "BRAKE_PAD", "Brake Pad Inspection", None, 6000,
                   "Replace when wear indicator is reached"),
    _make_schedule("honda", "CB Shine", 2006, None, "TIRE_ROTATION", "Tire Inspection", None, 5000,
                   "Check tread depth and inflation; front 29 psi, rear 33 psi"),
]

_HONDA_CBR600RR_SCHEDULES = [
    _make_schedule("honda", "CBR600RR", 2003, None, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 8000,
                   "Use Honda Ultra 4 10W-40 fully synthetic"),
    _make_schedule("honda", "CBR600RR", 2003, None, "AIR_FILTER", "Air Filter Replacement", None, 24000,
                   "Replace every 24,000 km or 2 years"),
    _make_schedule("honda", "CBR600RR", 2003, None, "SPARK_PLUG", "Spark Plug Replacement", None, 16000,
                   "Iridium plugs; check at 8000 km"),
    _make_schedule("honda", "CBR600RR", 2003, None, "COOLANT", "Coolant Replacement", 730, None,
                   "Use Honda Long Life Coolant 50% mix"),
    _make_schedule("honda", "CBR600RR", 2003, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "Use DOT 4; bleed lines at each change"),
    _make_schedule("honda", "CBR600RR", 2003, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "DID/RK O-ring chain; use O-ring safe lube"),
    _make_schedule("honda", "CBR600RR", 2003, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 25–35 mm"),
    _make_schedule("honda", "CBR600RR", 2003, None, "BRAKE_PAD", "Brake Pad Inspection", None, 8000,
                   "Front pads wear faster; inspect at each oil change"),
    _make_schedule("honda", "CBR600RR", 2003, None, "TIRE_REPLACEMENT", "Tire Wear Inspection", None, 8000,
                   "Replace at 1 mm tread depth; check pressure cold: front 36 psi, rear 42 psi"),
]

_HONDA_ACTIVA_SCHEDULES = [
    _make_schedule("honda", "Activa", 2001, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "Use Honda genuine 10W-30 scooter oil (JASO MB)"),
    _make_schedule("honda", "Activa", 2001, None, "AIR_FILTER", "Air Filter Clean", 180, 6000,
                   "Clean with air gun; replace at 12,000 km"),
    _make_schedule("honda", "Activa", 2001, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK CPR8EA-9 or equivalent"),
    _make_schedule("honda", "Activa", 2001, None, "TRANSMISSION_OIL", "Gear Oil Change", 180, 6000,
                   "Honda scooter gear oil; 85-90 mL capacity"),
    _make_schedule("honda", "Activa", 2001, None, "BRAKE_PAD", "Brake Pad/Shoe Inspection", None, 6000,
                   "Drum rear and disc front (post-2021); replace when worn to indicator"),
    _make_schedule("honda", "Activa", 2001, None, "TIRE_ROTATION", "Tire Check", None, 3000,
                   "Front 30 psi, rear 33 psi; check monthly"),
]

# ---------------------------------------------------------------------------
# Yamaha
# ---------------------------------------------------------------------------

_YAMAHA_R15_SCHEDULES = [
    _make_schedule("yamaha", "YZF-R15", 2008, None, "ENGINE_OIL", "Engine Oil & Filter Change", 180, 5000,
                   "Yamalube 10W-40 fully synthetic; 1.05 L capacity"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "AIR_FILTER", "Air Filter Replacement", None, 20000,
                   "Replace every 20,000 km or when damaged"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK CR9E or Yamaha OEM; gap 0.6–0.7 mm"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "COOLANT", "Coolant Check/Replace", 730, None,
                   "Yamaha coolant or equivalent; replace every 2 years"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "EK/DID chain; use non-fling chain spray"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 35–45 mm (check owner manual for specific version)"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4; bleed front and rear calipers"),
    _make_schedule("yamaha", "YZF-R15", 2008, None, "BRAKE_PAD", "Brake Pad Inspection", None, 8000,
                   "Replace at wear indicator groove"),
]

_YAMAHA_FZ_SCHEDULES = [
    _make_schedule("yamaha", "FZ Series", 2008, None, "ENGINE_OIL", "Engine Oil Change", 180, 5000,
                   "Yamalube 10W-40 semi-synthetic; 1.0 L capacity"),
    _make_schedule("yamaha", "FZ Series", 2008, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000,
                   "Clean at 6000 km; replace at 18,000 km"),
    _make_schedule("yamaha", "FZ Series", 2008, None, "SPARK_PLUG", "Spark Plug Check/Replace", None, 12000,
                   "NGK CR8E"),
    _make_schedule("yamaha", "FZ Series", 2008, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "Sealed chain; lube with spray chain lubricant"),
    _make_schedule("yamaha", "FZ Series", 2008, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 20–30 mm; check alignment marks on swingarm"),
    _make_schedule("yamaha", "FZ Series", 2008, None, "BRAKE_OIL", "Brake Fluid Check", 365, None,
                   "DOT 3 or DOT 4; maintain fluid level in reservoir"),
]

_YAMAHA_MT07_SCHEDULES = [
    _make_schedule("yamaha", "MT-07", 2014, None, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 10000,
                   "Yamalube 10W-40 fully synthetic; 3.2 L with filter change"),
    _make_schedule("yamaha", "MT-07", 2014, None, "AIR_FILTER", "Air Filter Replacement", None, 40000,
                   "Paper element; inspect at 20,000 km"),
    _make_schedule("yamaha", "MT-07", 2014, None, "SPARK_PLUG", "Spark Plug Replacement", None, 40000,
                   "Iridium plugs LMAR8AI-8; check gap at 20,000 km"),
    _make_schedule("yamaha", "MT-07", 2014, None, "COOLANT", "Coolant Replacement", 730, None,
                   "Yamaha Coolant / Antifreeze or equivalent; 50/50 with distilled water"),
    _make_schedule("yamaha", "MT-07", 2014, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4 both circuits; flush at 2-year intervals"),
    _make_schedule("yamaha", "MT-07", 2014, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("yamaha", "MT-07", 2014, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 35–45 mm"),
]

# ---------------------------------------------------------------------------
# KTM
# ---------------------------------------------------------------------------

_KTM_DUKE390_SCHEDULES = [
    _make_schedule("ktm", "Duke 390", 2013, None, "ENGINE_OIL", "Engine Oil & Filter Change", 180, 7500,
                   "Motorex Power Synt 4T 10W-50 fully synthetic; 1.6 L with filter"),
    _make_schedule("ktm", "Duke 390", 2013, None, "AIR_FILTER", "Air Filter Replacement", None, 15000,
                   "Replace every 15,000 km or annually in dusty conditions"),
    _make_schedule("ktm", "Duke 390", 2013, None, "SPARK_PLUG", "Spark Plug Replacement", None, 15000,
                   "NGK LMAR8AI-8 iridium"),
    _make_schedule("ktm", "Duke 390", 2013, None, "COOLANT", "Coolant Replacement", 730, None,
                   "Motorex Anti Freeze 4.1 or equivalent; 50/50 mix"),
    _make_schedule("ktm", "Duke 390", 2013, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4; flush both circuits biennially"),
    _make_schedule("ktm", "Duke 390", 2013, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "X-ring chain; Motorex chain lube recommended"),
    _make_schedule("ktm", "Duke 390", 2013, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 5–7 mm (KTM uses a tighter spec than most)"),
    _make_schedule("ktm", "Duke 390", 2013, None, "BRAKE_PAD", "Brake Pad Inspection", None, 7500,
                   "Bybre calipers; inspect at every service"),
]

_KTM_RC390_SCHEDULES = [
    _make_schedule("ktm", "RC 390", 2014, None, "ENGINE_OIL", "Engine Oil & Filter Change", 180, 7500,
                   "Motorex Power Synt 4T 10W-50; 1.6 L"),
    _make_schedule("ktm", "RC 390", 2014, None, "AIR_FILTER", "Air Filter Replacement", None, 15000),
    _make_schedule("ktm", "RC 390", 2014, None, "COOLANT", "Coolant Replacement", 730, None,
                   "50/50 distilled water and coolant"),
    _make_schedule("ktm", "RC 390", 2014, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4 both circuits"),
    _make_schedule("ktm", "RC 390", 2014, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("ktm", "RC 390", 2014, None, "BRAKE_PAD", "Brake Pad Inspection", None, 7500,
                   "Track use: inspect every 2000 km"),
    _make_schedule("ktm", "RC 390", 2014, None, "TIRE_REPLACEMENT", "Tire Wear Check", None, 6000,
                   "Track compound tires wear faster; monitor tread depth"),
]

_KTM_ADVENTURE390_SCHEDULES = [
    _make_schedule("ktm", "390 Adventure", 2020, None, "ENGINE_OIL", "Engine Oil & Filter Change", 180, 7500,
                   "Motorex 10W-50 fully synthetic"),
    _make_schedule("ktm", "390 Adventure", 2020, None, "AIR_FILTER", "Air Filter Replacement", None, 15000,
                   "More frequent in off-road/dusty conditions"),
    _make_schedule("ktm", "390 Adventure", 2020, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "More frequent in wet or off-road conditions"),
    _make_schedule("ktm", "390 Adventure", 2020, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000),
    _make_schedule("ktm", "390 Adventure", 2020, None, "COOLANT", "Coolant Replacement", 730, None),
    _make_schedule("ktm", "390 Adventure", 2020, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4"),
    _make_schedule("ktm", "390 Adventure", 2020, None, "SPARK_PLUG", "Spark Plug Replacement", None, 15000),
]

# ---------------------------------------------------------------------------
# Royal Enfield
# ---------------------------------------------------------------------------

_RE_CLASSIC350_SCHEDULES = [
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "RE genuine 15W-50 semi-synthetic; 2.25 L"),
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000,
                   "Foam/paper element; clean at 6000 km, replace at 12,000 km"),
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK BPR6ES or equivalent"),
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "CHAIN_LUBE", "Chain Lubrication", None, 700,
                   "Use non-fling chain lube; more often in rain"),
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1400,
                   "Free play: 25–35 mm"),
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "BRAKE_OIL", "Brake Fluid Check/Replace", 365, None,
                   "DOT 3 or 4; check reservoir monthly"),
    _make_schedule("royal_enfield", "Classic 350", 2009, None, "BATTERY", "Battery Maintenance Check", 180, None,
                   "Check terminals and electrolyte level (if non-sealed)"),
]

_RE_HIMALAYAN_SCHEDULES = [
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "ENGINE_OIL", "Engine Oil & Filter Change", 180, 5000,
                   "RE genuine 10W-40 semi-synthetic; 2.5 L with filter"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000,
                   "Foam element; inspect more frequently in off-road/dusty conditions"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK DCPR8E"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "Critical on adventure rides; lube after water crossings"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 25–35 mm"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "COOLANT", "Coolant Check/Replace", 730, None,
                   "Himalayan 411 is liquid-cooled; use OEM coolant"),
    _make_schedule("royal_enfield", "Himalayan", 2016, None, "TIRE_ROTATION", "Tire Inspection & Pressure", None, 2000,
                   "Off-road tires wear irregularly; check cold pressure: front 21 psi, rear 25 psi"),
]

# ---------------------------------------------------------------------------
# Kawasaki
# ---------------------------------------------------------------------------

_KAWASAKI_NINJA300_SCHEDULES = [
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 7500,
                   "Kawasaki 10W-40 4-stroke oil; 1.9 L with filter"),
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "AIR_FILTER", "Air Filter Replacement", None, 15000),
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "SPARK_PLUG", "Spark Plug Replacement", None, 15000,
                   "NGK CR9EIA-9 iridium"),
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "COOLANT", "Coolant Replacement", 730, None,
                   "Kawasaki Long-Life Coolant or equivalent"),
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4; flush both circuits"),
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("kawasaki", "Ninja 300", 2012, 2018, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 25–35 mm"),
]

_KAWASAKI_Z900_SCHEDULES = [
    _make_schedule("kawasaki", "Z900", 2017, None, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 12000,
                   "Kawasaki 10W-40 MA2 fully synthetic; 3.8 L with filter"),
    _make_schedule("kawasaki", "Z900", 2017, None, "AIR_FILTER", "Air Filter Replacement", None, 24000),
    _make_schedule("kawasaki", "Z900", 2017, None, "SPARK_PLUG", "Spark Plug Replacement", None, 24000,
                   "NGK LMAR8AI-8 iridium"),
    _make_schedule("kawasaki", "Z900", 2017, None, "COOLANT", "Coolant Replacement", 730, None),
    _make_schedule("kawasaki", "Z900", 2017, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4"),
    _make_schedule("kawasaki", "Z900", 2017, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("kawasaki", "Z900", 2017, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 25–35 mm"),
    _make_schedule("kawasaki", "Z900", 2017, None, "BRAKE_PAD", "Brake Pad Inspection", None, 12000),
]

# ---------------------------------------------------------------------------
# Suzuki
# ---------------------------------------------------------------------------

_SUZUKI_GIXXER_SCHEDULES = [
    _make_schedule("suzuki", "Gixxer", 2014, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "Suzuki genuine 10W-40 semi-synthetic or fully synthetic"),
    _make_schedule("suzuki", "Gixxer", 2014, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000,
                   "Clean at 6000 km; replace at 24,000 km"),
    _make_schedule("suzuki", "Gixxer", 2014, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK CR8E"),
    _make_schedule("suzuki", "Gixxer", 2014, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("suzuki", "Gixxer", 2014, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 20–30 mm"),
    _make_schedule("suzuki", "Gixxer", 2014, None, "BRAKE_OIL", "Brake Fluid Check", 365, None,
                   "DOT 3 or DOT 4"),
]

_SUZUKI_GSXR600_SCHEDULES = [
    _make_schedule("suzuki", "GSX-R600", 1992, None, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 7500,
                   "Suzuki 10W-40 fully synthetic; 3.8 L with filter"),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "AIR_FILTER", "Air Filter Replacement", None, 24000),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "SPARK_PLUG", "Spark Plug Replacement", None, 24000,
                   "NGK CR9EIA-9"),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "COOLANT", "Coolant Replacement", 730, None),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4"),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000),
    _make_schedule("suzuki", "GSX-R600", 1992, None, "TIRE_REPLACEMENT", "Tire Check", None, 6000),
]

# ---------------------------------------------------------------------------
# Bajaj
# ---------------------------------------------------------------------------

_BAJAJ_PULSAR_SCHEDULES = [
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "Bajaj genuine 20W-50 mineral or 10W-40 semi-synthetic; 1.0 L"),
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000,
                   "Dry foam element; clean at 6000 km, replace at 12,000 km"),
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "SPARK_PLUG", "Spark Plug Replacement", None, 10000,
                   "NGK BPR7ES or Bosch equivalent"),
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 20–30 mm"),
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "BRAKE_OIL", "Brake Fluid Check", 365, None,
                   "DOT 3 or DOT 4; check reservoir level monthly"),
    _make_schedule("bajaj", "Pulsar 150", 2001, None, "BATTERY", "Battery Check", 180, None,
                   "Check terminal corrosion; MF battery requires no topping up"),
]

_BAJAJ_DOMINAR_SCHEDULES = [
    _make_schedule("bajaj", "Dominar 400", 2017, None, "ENGINE_OIL", "Engine Oil & Filter Change", 180, 5000,
                   "Bajaj-approved 10W-50 fully synthetic; ~2.1 L with filter"),
    _make_schedule("bajaj", "Dominar 400", 2017, None, "AIR_FILTER", "Air Filter Replacement", None, 20000),
    _make_schedule("bajaj", "Dominar 400", 2017, None, "SPARK_PLUG", "Spark Plug Replacement", None, 15000,
                   "NGK iridium"),
    _make_schedule("bajaj", "Dominar 400", 2017, None, "COOLANT", "Coolant Replacement", 730, None),
    _make_schedule("bajaj", "Dominar 400", 2017, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("bajaj", "Dominar 400", 2017, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 20–30 mm"),
    _make_schedule("bajaj", "Dominar 400", 2017, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4"),
]

# ---------------------------------------------------------------------------
# TVS
# ---------------------------------------------------------------------------

_TVS_APACHE_SCHEDULES = [
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "TVS Genuine SAE 10W-30 semi-synthetic; 0.9 L"),
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000),
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "SPARK_PLUG", "Spark Plug Check/Replace", None, 10000,
                   "NGK CPR8EA-9"),
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 20–30 mm"),
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "BRAKE_OIL", "Brake Fluid Check", 365, None,
                   "DOT 3 or DOT 4"),
    _make_schedule("tvs", "Apache RTR 160", 2008, None, "BATTERY", "Battery Terminal Check", 180, None),
]

_TVS_NTORQ_SCHEDULES = [
    _make_schedule("tvs", "NTorq 125", 2018, None, "ENGINE_OIL", "Engine Oil Change", 90, 3000,
                   "TVS Genuine 10W-30 JASO MB scooter oil; 0.85 L"),
    _make_schedule("tvs", "NTorq 125", 2018, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000),
    _make_schedule("tvs", "NTorq 125", 2018, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK CR7HSA"),
    _make_schedule("tvs", "NTorq 125", 2018, None, "TRANSMISSION_OIL", "Gear Oil Change", 180, 6000,
                   "TVS scooter gear oil; 100 mL"),
    _make_schedule("tvs", "NTorq 125", 2018, None, "BRAKE_PAD", "Brake Pad/Shoe Inspection", None, 6000),
    _make_schedule("tvs", "NTorq 125", 2018, None, "TIRE_ROTATION", "Tire Pressure Check", None, 1000,
                   "Front 30 psi, rear 33 psi; check before every long ride"),
]

# ---------------------------------------------------------------------------
# Hero
# ---------------------------------------------------------------------------

_HERO_SPLENDOR_SCHEDULES = [
    _make_schedule("hero", "Splendor Plus", 1994, None, "ENGINE_OIL", "Engine Oil Change", 60, 2500,
                   "Hero genuine 10W-30 mineral oil; 0.9 L"),
    _make_schedule("hero", "Splendor Plus", 1994, None, "AIR_FILTER", "Air Filter Clean/Replace", 90, 3000,
                   "Clean at every service; replace at 12,000 km"),
    _make_schedule("hero", "Splendor Plus", 1994, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK BPR6ES or Hero OEM"),
    _make_schedule("hero", "Splendor Plus", 1994, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("hero", "Splendor Plus", 1994, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 20–30 mm"),
    _make_schedule("hero", "Splendor Plus", 1994, None, "BRAKE_OIL", "Brake Fluid Check", 365, None,
                   "DOT 3"),
]

_HERO_XTREME_SCHEDULES = [
    _make_schedule("hero", "Xtreme 160R", 2020, None, "ENGINE_OIL", "Engine Oil & Filter Change", 90, 3000,
                   "Hero genuine 10W-40 semi-synthetic; 1.2 L with filter"),
    _make_schedule("hero", "Xtreme 160R", 2020, None, "AIR_FILTER", "Air Filter Clean/Replace", 180, 6000),
    _make_schedule("hero", "Xtreme 160R", 2020, None, "SPARK_PLUG", "Spark Plug Replacement", None, 12000,
                   "NGK CPR8EA-9"),
    _make_schedule("hero", "Xtreme 160R", 2020, None, "CHAIN_LUBE", "Chain Lubrication", None, 500),
    _make_schedule("hero", "Xtreme 160R", 2020, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000),
    _make_schedule("hero", "Xtreme 160R", 2020, None, "BRAKE_OIL", "Brake Fluid Check", 365, None,
                   "DOT 4; disc brakes both ends"),
    _make_schedule("hero", "Xtreme 160R", 2020, None, "BATTERY", "Battery Terminal Check", 180, None),
]

# ---------------------------------------------------------------------------
# BMW Motorrad
# ---------------------------------------------------------------------------

_BMW_GS1250_SCHEDULES = [
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 10000,
                   "BMW Motorrad 5W-40 fully synthetic; 4.25 L with filter"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "AIR_FILTER", "Air Filter Replacement", None, 20000,
                   "Paper element; inspect annually"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "SPARK_PLUG", "Spark Plug Replacement", None, 20000,
                   "NGK iridium — do not re-gap"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "COOLANT", "Coolant Replacement", 730, None,
                   "BMW Motorrad Coolant Antifreeze or equivalent"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4 — ABS bikes require professional bleeding"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "TRANSMISSION_OIL", "Final Drive Oil Change", 365, 20000,
                   "BMW Hypoid Oil 75W-90 in the shaft drive final drive unit"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "TIRE_REPLACEMENT", "Tire Wear Inspection", None, 8000,
                   "Metzeler Tourance Next 2 or equivalent ADV tire; front 2.9 bar, rear 2.9 bar"),
    _make_schedule("bmw_motorrad", "R 1250 GS", 2019, None, "BATTERY", "Battery Maintenance", 180, None,
                   "Use CTEK charger if stored; AGM battery — do not use conventional charger"),
]

_BMW_S1000RR_SCHEDULES = [
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "ENGINE_OIL", "Engine Oil & Filter Change", 365, 10000,
                   "BMW Motorrad 5W-40 fully synthetic; 4.0 L with filter"),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "SPARK_PLUG", "Spark Plug Replacement", None, 20000,
                   "NGK LMAR8AI-8 iridium"),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "COOLANT", "Coolant Replacement", 730, None),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "BRAKE_OIL", "Brake Fluid Replacement", 730, None,
                   "DOT 4; race use — change every season"),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "CHAIN_LUBE", "Chain Lubrication", None, 500,
                   "X-ring chain; use dry chain lube for track use"),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "CHAIN_ADJUSTMENT", "Chain Adjustment", None, 1000,
                   "Free play: 25–35 mm"),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "BRAKE_PAD", "Brake Pad Inspection", None, 5000,
                   "Track use: inspect every session; Brembo M50 calipers"),
    _make_schedule("bmw_motorrad", "S 1000 RR", 2009, None, "TIRE_REPLACEMENT", "Tire Wear Check", None, 4000,
                   "Track days significantly reduce tire life"),
]


class OEMScheduleScraper(BaseScraper):
    """Returns curated OEM maintenance schedules — no HTTP requests."""

    # Combine all schedules into the class
    ALL_SCHEDULES: list[RegistrySchedule] = (
        _HONDA_CB_SHINE_SCHEDULES
        + _HONDA_CBR600RR_SCHEDULES
        + _HONDA_ACTIVA_SCHEDULES
        + _YAMAHA_R15_SCHEDULES
        + _YAMAHA_FZ_SCHEDULES
        + _YAMAHA_MT07_SCHEDULES
        + _KTM_DUKE390_SCHEDULES
        + _KTM_RC390_SCHEDULES
        + _KTM_ADVENTURE390_SCHEDULES
        + _RE_CLASSIC350_SCHEDULES
        + _RE_HIMALAYAN_SCHEDULES
        + _KAWASAKI_NINJA300_SCHEDULES
        + _KAWASAKI_Z900_SCHEDULES
        + _SUZUKI_GIXXER_SCHEDULES
        + _SUZUKI_GSXR600_SCHEDULES
        + _BAJAJ_PULSAR_SCHEDULES
        + _BAJAJ_DOMINAR_SCHEDULES
        + _TVS_APACHE_SCHEDULES
        + _TVS_NTORQ_SCHEDULES
        + _HERO_SPLENDOR_SCHEDULES
        + _HERO_XTREME_SCHEDULES
        + _BMW_GS1250_SCHEDULES
        + _BMW_S1000RR_SCHEDULES
    )

    def get_all_schedules(self) -> list[RegistrySchedule]:
        """Return all curated OEM maintenance schedules."""
        return list(self.ALL_SCHEDULES)

    def get_schedules_for_make(self, make: str) -> list[RegistrySchedule]:
        """Return schedules filtered by make (case-insensitive)."""
        make_lower = make.lower()
        return [s for s in self.ALL_SCHEDULES if s.make.lower() == make_lower]
