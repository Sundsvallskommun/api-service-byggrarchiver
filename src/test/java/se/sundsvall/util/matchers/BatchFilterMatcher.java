package se.sundsvall.util.matchers;

import lombok.AllArgsConstructor;
import org.mockito.ArgumentMatcher;
import se.tekis.servicecontract.BatchFilter;

@AllArgsConstructor
public class BatchFilterMatcher implements ArgumentMatcher<BatchFilter> {

    private BatchFilter left;

    @Override
    public boolean matches(BatchFilter right) {
        if (right == null) {
            return false;
        }
        return left.getLowerExclusiveBound().isEqual(right.getLowerExclusiveBound()) &&
                left.getUpperInclusiveBound().isEqual(right.getUpperInclusiveBound());
    }
}
