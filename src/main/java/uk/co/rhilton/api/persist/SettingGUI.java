package uk.co.rhilton.api.persist;

import shmarovfedor.api.problem.Problem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class SettingGUI {

    private final Problem problem;
    private final List<Entry<?>> entries = new ArrayList<>();

    public SettingGUI(Problem problem) {
        this.problem = problem;
    }

    public <T> SettingGUI with(Setting<T> setting, String displayName, Predicate<T> validityCheck) {
        entries.add(new Entry<T>(setting, displayName, validityCheck));
        return this;
    }

    public void fillAndOpen() {
        var frame = new JFrame("Change Settings") {{
//            setSize(new Dimension(200, 200));
            setLocationRelativeTo(problem.frame());
            setResizable(false);
            setLayout(new GridLayout(entries.size() + 1, 2));
        }};

        var config = problem.config();
        entries.forEach(e -> e.add(frame, config));

        var confirm = new JButton("Confirm");
        confirm.addActionListener(e -> {
            var invalid = entries.stream().filter(Entry::isInvalid).findFirst();
            if (invalid.isPresent()) {
                JOptionPane.showMessageDialog(frame, "Invalid value for field " + invalid.get().displayName());
                return;
            }
            entries.forEach(ex -> ex.apply(config));
            frame.dispose();
        });
        frame.add(confirm);

        var reset = new JButton("Reset");
        reset.addActionListener(e -> {
            entries.forEach(ex -> ex.reset(config));
            frame.dispose();
        });
        frame.add(reset);

        frame.setMinimumSize(new Dimension(200, 200));
        frame.pack();
        frame.setVisible(true);
    }

    private static final class Entry<T> {
        private final Setting<T> setting;
        private final String displayName;
        private final Predicate<T> validityCheck;
        private Component component;


        private Entry(Setting<T> setting, String displayName, Predicate<T> validityCheck) {
            this.setting = setting;
            this.displayName = displayName;
            this.validityCheck = validityCheck;
        }

        public void add(JFrame frame, SettingStorage config) {
            var label = new JLabel(displayName);
            var comp = setting.settingField(config.valueOf(setting));
            frame.getContentPane().add(label);
            frame.getContentPane().add(comp);
            this.component = comp;
        }

        public boolean isInvalid() {
            return !validityCheck.test(setting.fromField(component));
        }

        public void apply(SettingStorage config) {
            config.save(setting, setting.fromField(component));
        }

        public void reset(SettingStorage config) {
            config.remove(setting);
        }

        public Setting<T> setting() {
            return setting;
        }

        public String displayName() {
            return displayName;
        }

        public Predicate<T> validityCheck() {
            return validityCheck;
        }

        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Entry<?>) obj;
            return Objects.equals(this.setting, that.setting) &&
                    Objects.equals(this.displayName, that.displayName) &&
                    Objects.equals(this.validityCheck, that.validityCheck);
        }

        public int hashCode() {
            return Objects.hash(setting, displayName, validityCheck);
        }

        public String toString() {
            return "Entry[" +
                    "setting=" + setting + ", " +
                    "displayName=" + displayName + ", " +
                    "validityCheck=" + validityCheck + ']';
        }

    }

}
