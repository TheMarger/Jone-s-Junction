import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import javax.sound.sampled.*;

/**
 * EventSlotMachine — plays WAV files packaged in the "music" resource folder.
 *
 * Place BGMusic.wav, Scroll.wav, Success.wav in src/music/ so they are available
 * as resources at runtime at /music/BGMusic.wav etc.
 */
public class EventSlotMachine extends JPanel {

    private static final String[] EVENTS = new String[] {
            "Remove RED",
            "Remove BLACK",
            "Remove HEARTS",
            "Remove SPADES",
            "Remove DIAMONDS",
            "Remove CLUBS",
            "Remove ODD",
            "Remove EVEN"
    };

    private static final int FPS = 60;

    private final Reel reel;
    private final Timer animator;
    private final Random rnd = new Random();

    // lever visuals and hit area
    private Rectangle2D leverBounds = new Rectangle2D.Double();
    private double leverAngle = 0.0; // animate return

    private String lastResult = "";

    // debug / diagnostics
    private String errorMessage = null;
    private boolean showDebug = true; // set true to display internal state overlay

    // ---------- Audio players (resources) ----------
    private WavPlayer bgPlayer;
    private WavPlayer scrollPlayer;
    private WavPlayer successPlayer;

    // volumes in [0.0 .. 1.0]
    private static final double BG_VOLUME = 0.25;
    private static final double SCROLL_VOLUME = 0.80;
    private static final double SUCCESS_VOLUME = 1.00;

    // spin transition tracking
    private boolean prevSpinning = false;

    public EventSlotMachine() {
        setBackground(new Color(6,6,12));
        setLayout(new BorderLayout());
        setFocusable(true);

        // initialize audio players from resources inside package "music"
        // Resource paths are absolute from classpath root: "/music/Name.wav"
        try {
            bgPlayer = new WavPlayer(EventSlotMachine.class, "/sound/BGMusic.wav");
            if (bgPlayer.isLoaded()) {
                bgPlayer.setVolume(BG_VOLUME);
                bgPlayer.loop(); // start looped background music
            } else {
                System.err.println("BGMusic.wav not loaded (check resource path /sound/BGMusic.wav).");
            }
        } catch (Throwable t) {
            System.err.println("Failed to init BG player: " + t.getMessage());
            t.printStackTrace();
        }

        try {
            scrollPlayer = new WavPlayer(EventSlotMachine.class, "/sound/Scroll.wav");
            if (scrollPlayer.isLoaded()) scrollPlayer.setVolume(SCROLL_VOLUME);
            else System.err.println("Scroll.wav not loaded (check resource path /sound/Scroll.wav).");
        } catch (Throwable t) {
            System.err.println("Failed to init scroll player: " + t.getMessage());
            t.printStackTrace();
        }

        try {
            successPlayer = new WavPlayer(EventSlotMachine.class, "/sound/Success.wav");
            if (successPlayer.isLoaded()) successPlayer.setVolume(SUCCESS_VOLUME);
            else System.err.println("Success.wav not loaded (check resource path /sound/Success.wav).");
        } catch (Throwable t) {
            System.err.println("Failed to init success player: " + t.getMessage());
            t.printStackTrace();
        }

        reel = new Reel(EVENTS);

        animator = new Timer(1000 / FPS, e -> onTick());
        animator.start();

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                if (leverBounds.contains(p)) {
                    if (!reel.isSpinning()) {
                        pullLeverAnimation();
                        startSpin();
                    } else {
                        // force immediate complete to the chosen target
                        reel.forceStopNow();
                    }
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "lever");
        getActionMap().put("lever", new AbstractAction() { public void actionPerformed(ActionEvent e) {
            if (!reel.isSpinning()) { pullLeverAnimation(); startSpin(); } else { reel.forceStopNow(); }
        }});
    }

    private void onTick() {
        try {
            // fixed dt
            reel.update(1.0 / FPS);
            if (leverAngle > 0) leverAngle = Math.max(0.0, leverAngle - 0.06);

            // detect transitions to control sound
            boolean curSpinning = reel.isSpinning();
            if (!prevSpinning && curSpinning) {
                // spin started -> start scroll loop
                if (scrollPlayer != null && scrollPlayer.isLoaded()) {
                    scrollPlayer.loop();
                }
            } else if (prevSpinning && !curSpinning) {
                // spin finished -> stop scroll and play success once
                if (scrollPlayer != null && scrollPlayer.isLoaded()) scrollPlayer.stop();
                if (successPlayer != null && successPlayer.isLoaded()) successPlayer.playOnce();
            }
            prevSpinning = curSpinning;

            if (!reel.isSpinning() && !reel.isResultAssigned()) {
                // once reel finishes, assign final result (centerIndex) to display and footer
                lastResult = reel.getFinalCenterSymbol();
                reel.setResultAssigned(true);
                reel.pulse();
            }
            errorMessage = null;
        } catch (Throwable t) {
            // catch runtime errors so UI doesn't go completely black; show message on screen
            errorMessage = "Error: " + t.getClass().getSimpleName() + ": " + t.getMessage();
            t.printStackTrace();
        }
        repaint();
    }

    private void startSpin() {
        lastResult = "";
        reel.setResultAssigned(false);
        int h = Math.max(480, getHeight());
        int symbolH = Math.max(64, Math.max(64, h/10));
        int cycles = 3 + rnd.nextInt(4);
        int target = rnd.nextInt(EVENTS.length);
        int totalDurationMs = 1400 + cycles * 180 + rnd.nextInt(300);
        // Log chosen target to console for validation
        System.out.println("Spin started: targetIndex=" + target + " -> " + EVENTS[target] + " cycles=" + cycles + " duration=" + totalDurationMs);
        reel.startSpin(cycles, target, totalDurationMs, symbolH);
    }

    private void pullLeverAnimation() { leverAngle = 1.0; }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        try {
            int w = getWidth(), h = getHeight();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // background
            g.setPaint(new GradientPaint(0,0,new Color(8,4,10),0,h,new Color(20,8,46)));
            g.fillRect(0,0,w,h);

            int pad = Math.max(18, Math.min(w,h)/50);
            int leverArea = 140; // reserve area on the right for lever integration
            int cabX = pad, cabY = pad, cabW = w - pad*2 - leverArea, cabH = h - pad*2;

            // cabinet
            RoundRectangle2D cab = new RoundRectangle2D.Double(cabX, cabY, cabW, cabH, 40, 40);
            g.setPaint(new GradientPaint(cabX, cabY, new Color(24,10,30), cabX, cabY + cabH, new Color(95,20,120)));
            g.fill(cab);
            g.setColor(new Color(255,230,160,120)); g.setStroke(new BasicStroke(6f)); g.draw(cab);

            // header marquee
            drawMarquee(g, cabX + 20, cabY + 18, cabW - 40, 60);

            // glass area for reel
            int glassW = (int)(cabW * 0.68);
            int glassH = (int)(cabH * 0.56);
            int glassX = cabX + (cabW - glassW)/2;
            int glassY = cabY + 120;
            drawGlassReel(g, glassX, glassY, glassW, glassH);

            // lever integrated into the cabinet's right edge (seamless)
            int leverW = 90, leverH = Math.min(cabH - 80, 360);
            int lvX = cabX + cabW - leverW/2; // overlap the cabinet edge by half the lever width
            int lvY = cabY + (cabH - leverH) / 2;
            // draw a subtle seam so it looks integrated
            RoundRectangle2D seam = new RoundRectangle2D.Double(lvX-8, cabY+16, 12, cabH-32, 6,6);
            g.setColor(new Color(255,255,255,16)); g.fill(seam);

            drawLever(g, lvX, lvY, leverW, leverH);
            leverBounds = new Rectangle2D.Double(lvX, lvY, leverW, leverH);

            // footer
            drawFooter(g, cabX + 32, cabY + cabH - 48, cabW - 160, 36);


            if (errorMessage != null) {
                g.setColor(new Color(200,40,40));
                g.setFont(new Font("SansSerif", Font.BOLD, 18));
                g.drawString(errorMessage, 24, h - 24);
            }
        } catch (Throwable t) {
            // if painting itself crashes, try to show tiny fallback
            g.setColor(Color.BLACK); g.fillRect(0,0,getWidth(), getHeight());
            g.setColor(Color.RED); g.drawString("Rendering failed: " + t.getClass().getSimpleName() + ":" + t.getMessage(), 10, 20);
            t.printStackTrace();
        } finally {
            g.dispose();
        }
    }

    private void drawMarquee(Graphics2D g, int x, int y, int w, int h) {
        RoundRectangle2D mar = new RoundRectangle2D.Double(x, y, w, h, 16, 16);
        g.setColor(new Color(12,12,16,200)); g.fill(mar);
        g.setColor(new Color(255,255,255,18)); g.draw(mar);

        int dots = Math.max(14, w/36);
        int gap = w / dots;
        double phase = (System.currentTimeMillis() % 1400) / 1400.0;
        for (int i=0;i<dots;i++){
            float hue = i/(float)dots;
            Color c = Color.getHSBColor(hue, 0.85f, 0.95f);
            int cx = x + 12 + i*gap;
            int cy = y + h/2;
            double a = 0.55 + 0.45 * Math.sin(phase * 2*Math.PI + i * 0.3);
            int alpha = (int)(Math.max(0.15, a) * 255);
            alpha = Math.max(20, Math.min(255, alpha));
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            g.fillOval(cx-8, cy-8, 16, 16);
            g.setColor(new Color(0,0,0,60)); g.drawOval(cx-8, cy-8, 16, 16);
        }

        g.setFont(new Font("Serif", Font.BOLD, 24));
        g.setColor(new Color(255,230,180));
        String lbl = "RAM'S RECKONING";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(lbl, x + (w - fm.stringWidth(lbl))/2, y + h/2 + fm.getAscent()/2 - 6);
    }

    private void drawGlassReel(Graphics2D g, int x, int y, int w, int h) {
        RoundRectangle2D glass = new RoundRectangle2D.Double(x, y, w, h, 18, 18);
        g.setPaint(new GradientPaint(x, y, new Color(16,16,22,220), x, y + h, new Color(6,6,8,220)));
        g.fill(glass);
        g.setColor(new Color(255,255,255,22)); g.draw(glass);

        int innerPad = 28;
        int ix = x + innerPad, iy = y + innerPad, iw = w - innerPad*2, ih = h - innerPad*2;
        RoundRectangle2D inner = new RoundRectangle2D.Double(ix, iy, iw, ih, 12, 12);
        g.setColor(new Color(4,4,6,220)); g.fill(inner);

        // measured (live) symbolH used when NOT spinning. When spinning use the frozen height
        int measuredSymbolH = Math.max(64, ih/6);
        // inform reel of measured size only when not spinning so it doesn't interfere with a frozen spin height
        reel.measureSymbolH(measuredSymbolH);

        // choose the symbol height used for drawing this frame
        int drawSymbolH = (int)Math.max(48, Math.round(reel.isSpinning() ? reel.getFrozenSymbolH() : measuredSymbolH));

        int midY = iy + ih/2 - drawSymbolH/2;
        RoundRectangle2D center = new RoundRectangle2D.Double(ix + 8, midY, iw - 16, drawSymbolH, 10, 10);
        g.setColor(new Color(255,255,255,10)); g.fill(center);
        g.setColor(new Color(255,255,255,40)); g.draw(center);

        // rendering uses display center and offset (deterministic)
        int displayCenter = reel.getDisplayCenterIndex();
        double offset = reel.getDisplayOffset();
        int pool = EVENTS.length;

        Shape oldClip = g.getClip();
        g.setClip(new Rectangle(ix, iy, iw, ih));

        for (int r = -4; r <= 4; r++) {
            int idx = reel.wrappedIndex(displayCenter + r);
            String s = reel.getSymbol(idx);
            // use drawSymbolH consistently for positioning; offset is computed in the same frozen pixel units
            int drawY = (int)Math.round(iy + ih/2 + r*drawSymbolH + offset - drawSymbolH/2);

            RoundRectangle2D sym = new RoundRectangle2D.Double(ix + 16, drawY, iw - 32, drawSymbolH - 8, 12, 12);
            float dist = Math.abs((iy + ih/2) - (drawY + drawSymbolH/2)) / (float)drawSymbolH;
            float alpha = Math.max(0.18f, 1.0f - dist*0.85f);

            float hue = (idx % pool) / (float)pool;
            Color a = Color.getHSBColor(hue, 0.78f, 0.98f);
            Color b = Color.getHSBColor((hue + 0.25f)%1f, 0.78f, 0.62f);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setPaint(new GradientPaint(ix + 16, drawY, a, ix + 16, drawY + drawSymbolH, b));
            g.fill(sym);
            g.setComposite(AlphaComposite.SrcOver);

            g.setFont(new Font("SansSerif", Font.BOLD, Math.max(18, drawSymbolH/3)));
            FontMetrics fm = g.getFontMetrics();
            int sw = fm.stringWidth(s);
            int tx = ix + iw/2 - sw/2;
            int ty = drawY + drawSymbolH/2 + fm.getAscent()/2 - 6;
            g.setColor(new Color(0,0,0,160)); g.drawString(s, tx+2, ty+2);
            g.setColor(new Color(255,250,240)); g.drawString(s, tx, ty);
        }

        g.setClip(oldClip);
        GradientPaint gloss = new GradientPaint(ix, iy, new Color(255,255,255,38), ix, iy + ih/2, new Color(255,255,255,0));
        g.setPaint(gloss); g.fill(new RoundRectangle2D.Double(ix, iy, iw, ih/2, 12, 12));
        g.setColor(new Color(255,255,255,18)); g.draw(inner);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawLever(Graphics2D g, int x, int y, int w, int h) {
        // slightly overlapping the cabinet so it's visually integrated
        RoundRectangle2D base = new RoundRectangle2D.Double(x-12, y + h - 44, w + 24, 48, 16, 16);
        g.setColor(new Color(30,28,28)); g.fill(base);
        g.setColor(new Color(255,255,255,30)); g.draw(base);

        AffineTransform old = g.getTransform();
        double cx = x + w/2.0, cy = y + h - 36;
        g.rotate(-0.55 * leverAngle, cx, cy);

        RoundRectangle2D arm = new RoundRectangle2D.Double(x + w/2.0 - 10, y + 12, 20, h - 80, 12, 12);
        g.setColor(new Color(230,230,230)); g.fill(arm);
        g.setColor(new Color(255,255,255,90)); g.draw(arm);

        Ellipse2D knob = new Ellipse2D.Double(x - 8, y + h - 72, 64, 64);
        g.setPaint(new GradientPaint((float)knob.getBounds2D().getX(), (float)knob.getBounds2D().getY(), new Color(255,140,200), (float)knob.getBounds2D().getX(), (float)knob.getBounds2D().getY()+64, new Color(200,40,120)));
        g.fill(knob);
        g.setColor(new Color(255,255,255,90)); g.draw(knob);

        g.setTransform(old);
    }

    private void drawFooter(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(255,255,255,160));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("Pull lever to spin. Press SPACE to toggle. Click lever to stop early.", x, y + 16);
        if (!lastResult.isEmpty()){
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.setColor(new Color(255,220,130));
            g.drawString("Primary Result: " + lastResult, x + w - 300, y + 16);
        }
    }

    // ------------------ Reel (timeline-driven deterministic animation) ------------------
    private static class Reel {
        private final String[] symbols;
        private final int pool;

        // settled center (only changed after spin completes)
        private int centerIndex = 0;

        // display state derived from startCenter + pixelsAccum
        private int startCenter = 0;       // captured at spin start
        private double pixelsAccum = 0.0;  // how many pixels have been advanced since start
        private double totalPixels = 0.0;  // target total (calculated using baseSymbolHStart)
        private int totalShifts = 0;       // total symbol shifts to perform

        // base symbol height at spin start (frozen for the spin)
        private double baseSymbolH = 96.0;      // current measured size (may change when not spinning)
        private double baseSymbolHStart = 96.0; // frozen at spin start and used for shift math

        // timing / timeline
        private boolean spinning = false;
        private long startMs = 0;
        private int durationMs = 1200;

        private float pulseAlpha = 0f;
        private int targetIndex = 0;
        private boolean resultAssigned = false;

        Reel(String[] syms) { symbols = syms.clone(); pool = symbols.length; centerIndex = new Random().nextInt(pool); }

        /**
         * update the measured symbol height — only applies when not spinning so it
         * doesn't conflict with a frozen spin height used for deterministic maths.
         */
        void measureSymbolH(int h) { if (!spinning) baseSymbolH = Math.max(48, h); }

        // expose frozen height for the renderer so it can draw using the same pixels
        double getFrozenSymbolH() { return baseSymbolHStart; }

        void startSpin(int cycles, int targetIndex, int totalDurationMs, int symbolH) {
            // freeze symbol height at spin start so shifts and display math stay consistent
            this.baseSymbolH = Math.max(48, symbolH);
            this.baseSymbolHStart = this.baseSymbolH;

            this.durationMs = Math.max(300, totalDurationMs); // lower bound for safety
            this.startMs = System.currentTimeMillis();
            this.spinning = true;
            this.pulseAlpha = 0f;

            this.startCenter = centerIndex; // freeze starting center for deterministic display
            this.targetIndex = targetIndex;
            // compute shifts so positive pixelsAcc -> symbols move DOWN and final center equals target
            int shiftToTarget = (startCenter - targetIndex) % pool; if (shiftToTarget < 0) shiftToTarget += pool;
            this.totalShifts = cycles * pool + shiftToTarget;

            // totalPixels is computed using the frozen baseSymbolHStart
            this.totalPixels = totalShifts * baseSymbolHStart;
            this.pixelsAccum = 0.0;

            this.resultAssigned = false;
        }

        void forceStopNow() {
            // immediately finish to the chosen target by setting accumulator to totalPixels
            pixelsAccum = totalPixels;
            centerIndex = wrappedIndex(targetIndex);
            spinning = false;
            resultAssigned = true;
            pulseAlpha = 1.0f;
        }

        boolean isSpinning() { return spinning; }
        void pulse() { pulseAlpha = 1.0f; }
        void setResultAssigned(boolean v) { resultAssigned = v; }
        boolean isResultAssigned() { return resultAssigned; }

        // easing function for smooth start/stop (easeInOutCubic)
        private static double easeInOutCubic(double t) {
            if (t < 0.5) return 4.0 * t * t * t;
            double f = -2.0 * t + 2.0;
            return 1.0 - (f * f * f) / 2.0;
        }

        // update with dt seconds — timeline-driven: compute pixelsAccum directly from elapsed fraction
        void update(double dtSec) {
            if (pulseAlpha > 0) pulseAlpha = Math.max(0f, pulseAlpha - 0.03f);
            if (!spinning) return;

            long now = System.currentTimeMillis();
            double elapsed = now - startMs; // ms
            double frac = elapsed / (double)durationMs;
            if (frac >= 1.0) {
                // final frame — snap precisely
                pixelsAccum = totalPixels;
                centerIndex = wrappedIndex(targetIndex);
                spinning = false;
                resultAssigned = true;
                pulseAlpha = 1.0f;
                return;
            }

            // compute eased progress in [0,1) and set accumulator deterministically
            double progress = easeInOutCubic(Math.max(0.0, Math.min(1.0, frac)));
            pixelsAccum = totalPixels * progress;

            // guardrails
            if (pixelsAccum < 0) pixelsAccum = 0;
            if (pixelsAccum > totalPixels) pixelsAccum = totalPixels;
        }

        // --- accessors for rendering & debug ---
        int getDisplayCenterIndex() {
            // display uses integer shifts computed using the frozen symbol height
            int shiftsDone = (int)Math.floor(pixelsAccum / baseSymbolHStart + 1e-9);
            if (shiftsDone > totalShifts) shiftsDone = totalShifts;
            return wrappedIndex(startCenter - shiftsDone);
        }

        double getDisplayOffset() {
            int shiftsDone = (int)Math.floor(pixelsAccum / baseSymbolHStart + 1e-9);
            if (shiftsDone > totalShifts) shiftsDone = totalShifts;
            return pixelsAccum - shiftsDone * baseSymbolHStart;
        }

        double getOffset() { return getDisplayOffset(); }
        String getFinalCenterSymbol() { return symbols[wrappedIndex(centerIndex)]; }
        String getSymbol(int idx) { return symbols[wrappedIndex(idx)]; }
        int getCenterIndex() { return centerIndex; }
        int wrappedIndex(int idx) { int v = idx % pool; if (v<0) v+=pool; return v; }

        // debug accessors
        int startCenterDebug() { return startCenter; }
        int targetIndexDebug() { return targetIndex; }
        double pixelsAccumDebug() { return pixelsAccum; }
        double totalPixelsDebug() { return totalPixels; }
    }

    // ---------------- WAV helper class (loads resources via URL) ----------------
    private static class WavPlayer {
        private Clip clip;
        private FloatControl gainControl;
        private boolean loaded = false;
        private AudioInputStream audioStream;

        /**
         * owner: class used to locate resource (e.g. EventSlotMachine.class)
         * resourcePath: absolute resource path such as "/music/BGMusic.wav"
         */
        WavPlayer(Class<?> owner, String resourcePath) {
            try {
                URL res = owner.getResource(resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
                if (res == null) {
                    System.err.println("Resource not found: " + resourcePath + " (owner: " + owner.getName() + ")");
                    return;
                }
                // use AudioSystem to get stream from URL
                AudioInputStream ais = AudioSystem.getAudioInputStream(res);
                AudioFormat baseFormat = ais.getFormat();

                // Convert compressed formats to PCM sign if needed
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false);

                AudioInputStream dais = AudioSystem.getAudioInputStream(decodedFormat, ais);
                audioStream = dais;

                DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat);
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(dais);

                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                } else {
                    gainControl = null;
                }

                loaded = true;
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                System.err.println("WavPlayer load failed for '" + resourcePath + "': " + ex.getMessage());
                ex.printStackTrace();
            } catch (Throwable t) {
                System.err.println("Unexpected audio init error for '" + resourcePath + "': " + t.getMessage());
                t.printStackTrace();
            }
        }

        boolean isLoaded() { return loaded && clip != null; }

        /**
         * Set linear volume 0..1. Internally convert to decibels if MASTER_GAIN is present.
         */
        void setVolume(double volume01) {
            if (!isLoaded()) return;
            if (gainControl != null) {
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float dB;
                if (volume01 <= 0.0001) dB = min;
                else dB = (float) (20.0 * Math.log10(volume01));
                dB = Math.max(min, Math.min(max, dB));
                gainControl.setValue(dB);
            } else {
                // If MASTER_GAIN not available, we can't reliably set volume here.
            }
        }

        void loop() {
            if (!isLoaded()) return;
            clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }

        void playOnce() {
            if (!isLoaded()) return;
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }

        void stop() {
            if (!isLoaded()) return;
            clip.stop();
            clip.setFramePosition(0);
        }

        void close() {
            if (!isLoaded()) return;
            try {
                clip.stop();
                clip.close();
                if (audioStream != null) audioStream.close();
            } catch (IOException ignored) {}
            loaded = false;
        }
    }

    // ---------------- launcher ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Reckon with Ram");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            EventSlotMachine p = new EventSlotMachine();
            f.setContentPane(p);
            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.setSize(1280, 800);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
            p.requestFocusInWindow();

            // Ensure audio stops when window closes (clean up audio resources)
            f.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) {
                    try {
                        if (p.bgPlayer != null) p.bgPlayer.close();
                        if (p.scrollPlayer != null) p.scrollPlayer.close();
                        if (p.successPlayer != null) p.successPlayer.close();
                    } catch (Throwable ignored) {}
                }
            });
        });
    }
}
