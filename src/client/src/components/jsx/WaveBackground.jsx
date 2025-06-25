import React from "react";
import "../css/WaveBackground.css";
import "../../styles/theme.css"
export default function WaveBackground() {
    return (
        <>
            <div className="sea-wave-bg" aria-hidden="true">
                <svg className="svg-wave" viewBox="0 0 2880 120" preserveAspectRatio="none">
                    <g>
                        {/* Vague 1 */}
                        <path className="wave1"
                            d="M0 50 Q 360 110 720 50 T 1440 50 T 2160 50 T 2880 50 V120 H0 Z"
                            fill="var(--wave1)" opacity="0.8" />
                        {/* Vague 2 */}
                        <path className="wave2"
                            d="M0 70 Q 360 130 720 70 T 1440 70 T 2160 70 T 2880 70 V120 H0 Z"
                            fill="var(--wave2)" opacity="0.6" />
                        {/* Vague 3 */}
                        <path className="wave3"
                            d="M0 90 Q 360 150 720 90 T 1440 90 T 2160 90 T 2880 90 V120 H0 Z"
                            fill="var(--wave3)" opacity="0.45" />
                    </g>
                </svg>
            </div>
            <div className="sea-reflection">
                <div className="reflection-sun"></div>
            </div>
        </>
    );
}