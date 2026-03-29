"""
Hardcoded best practices for motorcycle and scooter maintenance.
No scraping performed — all data is curated from industry knowledge.
"""
from datetime import datetime

import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from models import BestPractice

_NOW = datetime.utcnow()

# ---------------------------------------------------------------------------
# ROUTINE category — is_template=True (can be applied as maintenance task templates)
# ---------------------------------------------------------------------------

_ROUTINE_PRACTICES: list[BestPractice] = [
    BestPractice(
        id="routine_chain_lube",
        category="ROUTINE",
        task_type="CHAIN_LUBE",
        title="Regular Chain Lubrication",
        description=(
            "Lubricate the drive chain every 500 km or after riding in rain. "
            "Apply lubricant to the inner side of the chain while rotating the wheel slowly. "
            "Wipe off excess to prevent fling. Use O-ring safe lube for O/X/Z-ring chains."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=None,
        template_interval_km=500,
        updated_at=_NOW,
    ),
    BestPractice(
        id="routine_chain_cleaning",
        category="ROUTINE",
        task_type="CHAIN_LUBE",
        title="Chain Cleaning",
        description=(
            "Clean the chain every 1000 km using a chain cleaner or kerosene and a brush. "
            "Never use a high-pressure washer directly on the chain — it forces water into links. "
            "Allow to dry completely before applying fresh lubricant."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=None,
        template_interval_km=1000,
        updated_at=_NOW,
    ),
    BestPractice(
        id="routine_chain_adjustment",
        category="ROUTINE",
        task_type="CHAIN_ADJUSTMENT",
        title="Chain Slack Adjustment",
        description=(
            "Check and adjust chain slack every 1000 km or whenever free play exceeds the "
            "manufacturer specification (typically 20–35 mm measured at the midpoint of the lower run). "
            "Always re-check after adjusting and torque axle nut to spec. Ensure alignment marks match."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=None,
        template_interval_km=1000,
        updated_at=_NOW,
    ),
    BestPractice(
        id="routine_tire_pressure",
        category="ROUTINE",
        task_type="TIRE_ROTATION",
        title="Tire Pressure Check",
        description=(
            "Check tire pressure when cold (bike not ridden for at least 3 hours) every 2 weeks or "
            "every 500 km. Under-inflated tires generate excess heat and increase wear; "
            "over-inflated tires reduce grip. Always use the pressure values stamped on the swingarm "
            "or owner manual — not the maximum printed on the tire sidewall."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=14,
        template_interval_km=500,
        updated_at=_NOW,
    ),
    BestPractice(
        id="routine_battery_terminals",
        category="ROUTINE",
        task_type="BATTERY",
        title="Battery Terminal Cleaning",
        description=(
            "Inspect and clean battery terminals every 6 months. Remove corrosion with a "
            "wire brush and a baking soda/water paste. Apply terminal grease or petroleum "
            "jelly after cleaning to prevent future corrosion. Check battery voltage — "
            "a healthy 12V battery reads 12.6–12.8V at rest."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=180,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="routine_air_filter_cleaning",
        category="ROUTINE",
        task_type="AIR_FILTER",
        title="Air Filter Cleaning",
        description=(
            "Inspect and clean the air filter every 3000 km. For paper elements, "
            "tap gently to dislodge dust or blow with low-pressure compressed air from the inside out. "
            "Never wash paper filters with water. Foam filters can be washed and re-oiled. "
            "In dusty or off-road conditions, inspect every 1000 km."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=None,
        template_interval_km=3000,
        updated_at=_NOW,
    ),
    BestPractice(
        id="routine_brake_fluid_check",
        category="ROUTINE",
        task_type="BRAKE_OIL",
        title="Brake Fluid Level Check",
        description=(
            "Check brake fluid level every 3 months or 3000 km. The reservoir window should "
            "show fluid between MIN and MAX marks. A dropping level indicates pad wear (normal) "
            "or a leak (urgent — do not ride). Fluid that has darkened significantly should be flushed. "
            "Use only the grade specified on the reservoir cap (DOT 3 or DOT 4)."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=True,
        template_interval_days=90,
        template_interval_km=3000,
        updated_at=_NOW,
    ),
]

# ---------------------------------------------------------------------------
# POST_SERVICE category — is_template=False (informational tips after a service)
# ---------------------------------------------------------------------------

_POST_SERVICE_PRACTICES: list[BestPractice] = [
    BestPractice(
        id="post_service_oil_change",
        category="POST_SERVICE",
        task_type="ENGINE_OIL",
        title="After Oil Change: Warm-Up Procedure",
        description=(
            "After an oil change, start the engine and let it idle for 5 minutes before riding hard. "
            "This allows fresh oil to circulate through all galleries and lubricate critical surfaces. "
            "Check for leaks around the drain plug and filter before setting off. "
            "Confirm oil level is within range on the sight glass or dipstick after the warm-up."
        ),
        applicable_to=["ALL"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="post_service_chain_lube",
        category="POST_SERVICE",
        task_type="CHAIN_LUBE",
        title="After Chain Lube: Let It Absorb",
        description=(
            "After applying chain lubricant, wait at least 10 minutes before riding. "
            "This allows the carrier solvent to evaporate and the lubricant to penetrate the chain links. "
            "Riding immediately causes the lube to fling off before it has set, reducing effectiveness "
            "and coating your rear wheel and tyre with lubricant — a safety hazard."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="post_service_brake_pad_replacement",
        category="POST_SERVICE",
        task_type="BRAKE_PAD",
        title="After Brake Pad Replacement: Bed-In Procedure",
        description=(
            "New brake pads require a bed-in procedure to transfer a thin layer of pad material "
            "onto the disc rotor for optimal stopping power. Perform 10 moderate stops from 50 km/h "
            "to approximately 10 km/h, allowing 30 seconds of cooling between each. "
            "Avoid hard braking for the first 200 km. Do not drag brakes or come to a complete stop "
            "during the bed-in — heat buildup without movement can glaze the pad surface."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="post_service_coolant_flush",
        category="POST_SERVICE",
        task_type="COOLANT",
        title="After Coolant Flush: Air Bubble Check",
        description=(
            "After a coolant flush, start the engine with the radiator or reservoir cap removed and "
            "let it reach operating temperature. Burp any air pockets by gently squeezing the "
            "radiator hoses and observing the coolant level drop. Top up with the correct "
            "premixed coolant as needed. Fit the cap and allow one full heat cycle (ride, cool down) "
            "before rechecking the level in the expansion tank."
        ),
        applicable_to=["BIKE"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
]

# ---------------------------------------------------------------------------
# MANUFACTURER category — is_template=False (general best practice tips)
# ---------------------------------------------------------------------------

_MANUFACTURER_PRACTICES: list[BestPractice] = [
    BestPractice(
        id="manufacturer_break_in_service",
        category="MANUFACTURER",
        task_type="ENGINE_OIL",
        title="Never Skip the Break-In Service",
        description=(
            "The first 1000 km service is the most important service your bike will ever receive. "
            "During break-in, microscopic metal particles from the engine's mating surfaces contaminate "
            "the oil. Changing this oil removes those particles, preventing them from accelerating wear "
            "on bearings and rings. Skipping it can permanently reduce engine longevity. "
            "Also check chain tension, tighten fasteners, and verify all fluid levels at this service."
        ),
        applicable_to=["ALL"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="manufacturer_oil_grade",
        category="MANUFACTURER",
        task_type="ENGINE_OIL",
        title="Use the Manufacturer-Specified Oil Grade",
        description=(
            "Using a thicker oil than specified (e.g., 20W-50 instead of 10W-40) in a modern engine "
            "can reduce oil pressure readings and mask wear indicators. Thicker oil also takes longer "
            "to reach critical components on cold starts — when most engine wear occurs. "
            "Always use the viscosity grade in the owner manual. For high-mileage engines, "
            "consult the manual before switching grades."
        ),
        applicable_to=["ALL"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="manufacturer_stainless_brake_lines",
        category="MANUFACTURER",
        task_type="BRAKE_OIL",
        title="Consider Stainless Steel Braided Brake Lines",
        description=(
            "OEM rubber brake hoses expand under pressure, contributing to a spongy lever feel — "
            "especially when the fluid is old or slightly aerated. Stainless steel braided lines "
            "resist expansion, providing a firmer, more consistent feel and improved modulation. "
            "They also last significantly longer than rubber lines, which degrade from inside out "
            "and can be difficult to inspect visually."
        ),
        applicable_to=["BIKE"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="manufacturer_valve_clearance",
        category="MANUFACTURER",
        task_type="ENGINE_OIL",
        title="Valve Clearance: The Often-Forgotten Service",
        description=(
            "Most four-stroke motorcycles require valve clearance inspection every 24,000–40,000 km "
            "or every 2–4 years. Tight valves cause hard starts, rough idle, and power loss; "
            "if ignored they can burn and require expensive head work. "
            "Many riders skip this because it requires partial engine disassembly, but it is "
            "far cheaper to adjust valves than to replace them. Check your service manual schedule."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="manufacturer_coolant_mixing",
        category="MANUFACTURER",
        task_type="COOLANT",
        title="Always Mix Coolant With Distilled Water",
        description=(
            "Never use tap water with coolant concentrate. Tap water contains minerals that "
            "precipitate and clog narrow coolant passages in motorcycle radiators. "
            "Use distilled or de-ionised water in a 50/50 mix for most climates, or "
            "up to 70% coolant concentrate in very cold climates. Premixed coolants are "
            "convenient but verify they are compatible with aluminium engine components."
        ),
        applicable_to=["BIKE"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
    BestPractice(
        id="manufacturer_tire_age",
        category="MANUFACTURER",
        task_type="TIRE_REPLACEMENT",
        title="Replace Tires by Age, Not Just Tread Depth",
        description=(
            "Motorcycle tires degrade chemically even when not in use. Rubber hardens and loses "
            "grip over time regardless of tread depth. Most manufacturers recommend replacing tires "
            "older than 5 years from the DOT manufacture date (stamped on the sidewall as a 4-digit code: "
            "WWYY — week and year). A 2019 tire with 5 mm of tread in 2025 is still an aged tire. "
            "Check the DOT code before buying secondhand bikes."
        ),
        applicable_to=["BIKE", "SCOOTER"],
        is_template=False,
        template_interval_days=None,
        template_interval_km=None,
        updated_at=_NOW,
    ),
]


def get_best_practices() -> list[BestPractice]:
    """Return the complete list of all hardcoded best practices."""
    return _ROUTINE_PRACTICES + _POST_SERVICE_PRACTICES + _MANUFACTURER_PRACTICES
