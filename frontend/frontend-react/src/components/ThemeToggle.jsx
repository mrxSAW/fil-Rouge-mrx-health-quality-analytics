import { useEffect, useState } from "react";

function ThemeToggle() {
  const [theme, setTheme] = useState(() => {
    return localStorage.getItem("theme") === "dark" ? "dark" : "light";
  });

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  function changeTheme() {
    setTheme((currentTheme) =>
      currentTheme === "light" ? "dark" : "light"
    );
  }

  return (
    <button
      type="button"
      className="theme-button"
      onClick={changeTheme}
      aria-label="Mode sombre"
      aria-pressed={theme === "dark"}
    >
      {theme === "light" ? "Mode sombre" : "Mode clair"}
    </button>
  );
}

export default ThemeToggle;