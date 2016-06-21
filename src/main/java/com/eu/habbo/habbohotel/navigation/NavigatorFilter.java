package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class NavigatorFilter {

    public final String viewName;

    public NavigatorFilter(String viewName) {
        this.viewName = viewName;
    }

    public void filter(Method method, Object value, List<SearchResultList> collection) {
        if (method == null) {
            return;
        }

        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return;
            }
        }

        for (SearchResultList result : collection) {
            if (!result.filter) {
                continue;
            }

            filterRooms(method, value, result.rooms);
        }
    }

    public void filterRooms(Method method, Object value, List<Room> result) {
        if (method == null) {
            return;
        }

        if (value instanceof String) {
            if (((String) value).isEmpty()) {
                return;
            }
        }

        List<Room> toRemove = new ArrayList<Room>();
        try {
            method.setAccessible(true);

            for (Room room : result) {
                Object o = method.invoke(room);
                if (o.getClass() == value.getClass()) {
                    if (o instanceof String) {
                        NavigatorFilterComparator comparator = Emulator.getGameEnvironment().getNavigatorManager().comperatorForField(method);

                        if (comparator != null) {
                            if (!applies(comparator, (String) o, (String) value)) {
                                toRemove.add(room);
                            }
                        } else {
                            toRemove.add(room);
                        }
                    } else if (o instanceof String[]) {
                        for (String s : (String[]) o) {
                            NavigatorFilterComparator comparator = Emulator.getGameEnvironment().getNavigatorManager().comperatorForField(method);

                            if (comparator != null) {
                                if (!applies(comparator, (String) o, (String) value)) {
                                    toRemove.add(room);
                                }
                            }
                        }
                    } else {
                        if (o != value) {
                            toRemove.add(room);
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            Emulator.getLogging().logErrorLine(e);
        } catch (IllegalAccessException e) {
            Emulator.getLogging().logErrorLine(e);
        } catch (IllegalArgumentException e) {
            Emulator.getLogging().logErrorLine(e);
        } catch (InvocationTargetException e) {
            Emulator.getLogging().logErrorLine(e);
        }

        result.removeAll(toRemove);
        toRemove.clear();
    }

    public abstract List<SearchResultList> getResult(Habbo habbo);

    private boolean applies(NavigatorFilterComparator comparator, String o, String value) {
        switch (comparator) {
            case CONTAINS:
                if (((String) o).contains((String) value)) {
                    return true;
                }
                break;

            case EQUALS:
                if (o.equals(value)) {
                    return true;
                }
                break;

            case EQUALS_IGNORE_CASE:
                if (((String) o).equalsIgnoreCase((String) value)) {
                    return true;
                }
        }

        return false;
    }
}
