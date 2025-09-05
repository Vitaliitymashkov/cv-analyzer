/**
 * Color utility functions for rating-based color generation
 * Provides HSL to RGB conversion and gradient color generation
 */

/**
 * Convert HSL color values to RGB
 * @param {number} h - Hue (0-360)
 * @param {number} s - Saturation (0-100)
 * @param {number} l - Lightness (0-100)
 * @returns {number[]} RGB values [r, g, b]
 */
export const hslToRgb = (h, s, l) => {
    h /= 360;
    s /= 100;
    l /= 100;
    
    const hue2rgb = (p, q, t) => {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1/6) return p + (q - p) * 6 * t;
        if (t < 1/2) return q;
        if (t < 2/3) return p + (q - p) * (2/3 - t) * 6;
        return p;
    };
    
    let r, g, b;
    if (s === 0) {
        r = g = b = l;
    } else {
        const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        const p = 2 * l - q;
        r = hue2rgb(p, q, h + 1/3);
        g = hue2rgb(p, q, h);
        b = hue2rgb(p, q, h - 1/3);
    }
    
    return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
};

/**
 * Convert RGB values to hex color string
 * @param {number} r - Red value (0-255)
 * @param {number} g - Green value (0-255)
 * @param {number} b - Blue value (0-255)
 * @returns {string} Hex color string
 */
export const rgbToHex = (r, g, b) => {
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
};

/**
 * Generate gradient colors from red to yellow to green based on rating
 * @param {number} rating - Current rating value
 * @param {number} minRating - Minimum rating value
 * @param {number} maxRating - Maximum rating value
 * @returns {Object} Color object with backgroundColor, borderColor, textColor, and colorScheme
 */
export const getRatingColors = (rating, minRating, maxRating) => {
    const range = maxRating - minRating;
    const percentage = (rating - minRating) / range;
    
    // Map percentage to hue: 0% = red (0°), 50% = yellow (60°), 100% = green (120°)
    const hue = percentage * 120;
    const saturation = 70; // Keep saturation consistent
    const lightness = 50; // Keep lightness consistent
    
    // Generate main color
    const [r, g, b] = hslToRgb(hue, saturation, lightness);
    const mainColor = rgbToHex(r, g, b);
    
    // Generate lighter background color
    const [rBg, gBg, bBg] = hslToRgb(hue, saturation, 15);
    const backgroundColor = `rgba(${rBg}, ${gBg}, ${bBg}, 0.1)`;
    
    // Generate border color
    const [rBorder, gBorder, bBorder] = hslToRgb(hue, saturation, 40);
    const borderColor = `rgba(${rBorder}, ${gBorder}, ${bBorder}, 0.3)`;
    
    // Generate text color with better contrast for both light and dark themes
    // Use darker colors for better visibility on light backgrounds
    const [rText, gText, bText] = hslToRgb(hue, saturation, 25);
    const textColor = `rgba(${rText}, ${gText}, ${bText}, 1)`;
    
    return {
        backgroundColor,
        borderColor,
        textColor,
        colorScheme: mainColor
    };
};

/**
 * Color constants for consistent theming
 */
export const COLOR_CONSTANTS = {
    SATURATION: 70,
    LIGHTNESS: 50,
    BACKGROUND_LIGHTNESS: 15,
    BORDER_LIGHTNESS: 40,
    TEXT_LIGHTNESS: 25,
    BACKGROUND_ALPHA: 0.1,
    BORDER_ALPHA: 0.3
};
